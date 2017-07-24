package app.core.business.logic;

import static app.core.business.model.mapping.ToBeChecked.Status.*;

import app.core.business.model.mapping.Contract;
import app.core.business.model.mapping.Coverage;
import app.core.business.model.mapping.Vehicle;
import app.core.business.model.mapping.sinister.Accident;
import app.core.business.model.mapping.sinister.PlainSinister;
import app.core.business.model.mapping.sinister.PlainSinister.Type;
import app.core.business.model.mapping.sinister.Sinister;
import app.core.web.model.databinding.command.sinister.AccidentSubmission;
import app.core.web.model.databinding.command.sinister.PlainSinisterSubmission;
import app.core.web.model.databinding.command.sinister.SinisterSubmission;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.net.URLConnection.guessContentTypeFromStream;

/**
 * Created by alexandremasanes on 21/06/2017.
 */
@Service
public class SinisterService  extends BaseService {

    @Value("${sinisterService.documentsDir.report}")
    private String reportDocumentDir;

    public synchronized void addSinister(
            SinisterSubmission submission,
            Contract           contract
    ) throws IOException {
        Sinister         sinister;
        Accident         thirdPartyAccident;
        Vehicle          contractVehicle, thirdPartyVehicle;
        Coverage         coverage;
        Type             type;
        byte[]           reportDocument;
        FileOutputStream outputStream;
        String           reportFileName;

        reportDocument = submission.getReportDocument();
        reportFileName = reportDocumentDir +
                dao.getNextId(Sinister.class) +
                guessContentTypeFromStream(
                        new ByteArrayInputStream(reportDocument)
                );

        outputStream = new FileOutputStream(reportFileName);
        outputStream.write(reportDocument);
        outputStream.close();

        contractVehicle = contract.getVehicle();

        if(submission instanceof AccidentSubmission) {
            sinister = new Accident(contractVehicle);

            thirdPartyVehicle = dao.findVehicleByRegistrationNumber(
                    ((AccidentSubmission) submission).getRegistrationNumber()
            );

            if(thirdPartyVehicle == null) {
                thirdPartyVehicle = new Vehicle();
                thirdPartyVehicle.setRegistrationNumber(
                        ((AccidentSubmission) submission).getRegistrationNumber()
                );
            }

            thirdPartyAccident = new Accident(thirdPartyVehicle);

            ((Accident)sinister).setAccident(thirdPartyAccident);

        } else {
            type = dao.find(Type.class, ((PlainSinisterSubmission)submission).getTypeId());

            sinister = new PlainSinister(contractVehicle, type);
        }

        coverage = new Coverage(sinister, contract);

        sinister.setCoverage(coverage);
        sinister.setComment(submission.getComment());
        sinister.setDate(submission.getDate());
        sinister.setTime(submission.getTime());
        sinister.setStatus(AWAITING);

        dao.save(sinister);
    }
}