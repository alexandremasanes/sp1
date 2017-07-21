package app.core.web.model.domaintransfer.customer;

import app.core.business.model.mapping.*;

import app.core.web.model.domaintransfer.Presentation;
import app.core.web.model.domaintransfer.publik.InsuranceDTO;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alexandremasanes on 13/04/2017.
 */
public class ContractDTO extends Presentation<Contract> {

    private long id;

    private ToBeChecked.Status status;

    private float amount;

    private boolean active;

    @SerializedName("subscription_date")
    private Date subscriptionDate;

    @SerializedName("insurance")
    private InsuranceDTO insuranceDTO;

    @SerializedName("vehicle")
    private VehicleDTO vehicleDTO;

    public ContractDTO(Contract contract) {
        super(contract);
        id               = contract.getId();
        status           = contract.getStatus();
        amount           = contract.getAmount();
        active           = contract.isActive();
        subscriptionDate = contract.getSubscriptionDate();
        insuranceDTO     = new InsuranceDTO(contract.getInsurance());
        vehicleDTO       = new VehicleDTO(contract.getVehicle());
    }
}
