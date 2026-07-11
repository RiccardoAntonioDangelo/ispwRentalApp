package org.example.controller.bean;

import org.example.controller.bean.util.AbstractBean;
import org.example.model.entity.rental.Payment;
import org.example.model.entity.rental.state.RentalS;
import org.example.model.services.rent.RentI;
import org.example.util.str.StrApp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalFormBean extends AbstractBean {

    private final RentalS rental;
    private String name = "";
    private String surname = "";
    private String email = "";
    private String ownerEmail="";

    private String phone = "";
    private String pickupLocation = "";
    private String productId = "";
    private LocalDate start;
    private LocalDate end;

    public RentalFormBean() { this.rental = new RentalS(); }

    public RentalFormBean(RentI rental) {
        this.rental = (rental != null) ? (RentalS) rental : new RentalS();
        fromRental((RentalS) rental);
    }


    /**
     * Inietta i dati estratti direttamente dal modello Rental all'interno dei campi del Bean.
     */
    public RentalFormBean fromRental(RentalS sourceRental) {
        if (sourceRental != null) {
            this.email = (sourceRental.getClientEmail() != null) ? sourceRental.getClientEmail() : "";
            this.phone = (sourceRental.getClientPhone() != null) ? sourceRental.getClientPhone() : "";
            this.pickupLocation = (sourceRental.getLocate() != null) ? sourceRental.getLocate() : "";
            this.productId = (sourceRental.getProductId() != null) ? sourceRental.getProductId() : ""; // Estrazione ID diretta
            this.start = sourceRental.getStartDate();
            this.end = sourceRental.getEndDate();
            this.ownerEmail= (sourceRental.getOwnerEmail() != null) ? sourceRental.getOwnerEmail() : "";
        }
        return this;
    }

    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    /**
     * Pre-popola il form di noleggio usando la sessione dell'utente loggato.
     * L'ID del prodotto può essere concatenato via Fluent API con .setProductId(...)
     */
    public RentalFormBean prePopulate(SessionBean session) {
        if (session != null) {
            this.name = (session.getSession().getUser().getName() != null) ? session.getSession().getUser().getName() : "";
            this.surname = (session.getSession().getUser().getSurname() != null) ? session.getSession().getUser().getSurname() : "";
            this.email = (session.getSession().getUser().getEmail() != null) ? session.getSession().getUser().getEmail() : "";
            this.phone = (session.getSession().getUser().getPhone() != null) ? session.getSession().getUser().getPhone() : "";
        }

        this.start = LocalDate.now();
        this.end = LocalDate.now().plusDays(1);

        return this;
    }

    /**
     * Metodo di validazione logica.
     */
    @Override
    public boolean isValid() {
        if (demoValid()) return demoValid();
        this.setErrorMessage("");

        if (name == null || name.trim().isEmpty() ||
                surname == null || surname.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty()) {
            this.setErrorMessage(StrApp.ERR_RENTAL_FIELDS_REQUIRED);
            return false;
        }

        if (pickupLocation == null || pickupLocation.trim().isEmpty()) {
            this.setErrorMessage(StrApp.ERR_RENTAL_PICKUP_REQUIRED);
            return false;
        }

        LocalDate startData = this.start;
        LocalDate endData = this.end;
        if (startData == null || endData == null) {
            this.setErrorMessage(StrApp.ERR_RENTAL_DATES_REQUIRED);
            return false;
        }
        if (startData.isBefore(LocalDate.now())) {
            this.setErrorMessage(StrApp.ERR_RENTAL_PAST_DATE);
            return false;
        }
        if (endData.isBefore(startData)) {
            this.setErrorMessage(StrApp.ERR_RENTAL_DATE_ORDER);
            return false;
        }

        if (productId == null || productId.trim().isEmpty()) {
            this.setErrorMessage(StrApp.ERR_RENTAL_NO_PRODUCT);
            return false;
        }

        return true;
    }

    /**
     * Sincronizza il modulo con l'entità Rental se i dati passano la validazione.
     */
    public boolean validateAndFill() {
        if (!isValid()) {
            return false;
        }

        // Passa direttamente la stringa productId estratta senza dipendere da ProductBean
        this.rental.updateFields(
                this.email,
                this.phone,
                this.productId,
                this.pickupLocation,
                this.start,
                this.end
        ).setOwnerEmail(ownerEmail);

        // Se valido, riversa i dati del Bean nel Modello Rental todo mancano dei campi
        return true;
    }

    /**
     * Ritorna una lista ridotta di dettagli del noleggio contrassegnati da prefissi.
     */
    public List<String> getReducedDetails() {
        List<String> details = new ArrayList<>();

        String startStr = (this.start != null) ? this.start.toString() : "N/A";
        String endStr = (this.end != null) ? this.end.toString() : "N/A";

        // Mettiamo l'ID come riferimento del titolo se non serve l'istanza intera
        details.add(StrApp.PREFIX_ID + (this.productId.isEmpty() ? "N/A" : this.productId));
        details.add(StrApp.PREFIX_START_DATE + startStr);
        details.add(StrApp.PREFIX_END_DATE + endStr);

        return details;
    }

    /**
     * Ritorna una lista completa di tutti i dettagli inseriti nel modulo marchiati per il parsing.
     */
    public List<String> getCompleteDetails() {
        List<String> details = getReducedDetails();

        String status = (this.rental != null && this.rental.getStatus() != null) ? this.rental.getStatus().toString() : "ATTIVO";

        details.add(StrApp.PREFIX_NAME + (this.name.isEmpty() ? "N/A" : this.name));
        details.add(StrApp.PREFIX_SURNAME + (this.surname.isEmpty() ? "N/A" : this.surname));
        details.add(StrApp.PREFIX_EMAIL + (this.email.isEmpty() ? "N/A" : this.email));
        details.add(StrApp.PREFIX_PHONE + (this.phone.isEmpty() ? "N/A" : this.phone));
        details.add(StrApp.PREFIX_PICKUP + (this.pickupLocation.isEmpty() ? "N/A" : this.pickupLocation));
        details.add(StrApp.PREFIX_STATUS + status);

        return details;
    }
    public String getPrefixOf(String detail) {
        if (detail.startsWith(StrApp.PREFIX_ID))      return StrApp.PREFIX_ID;
        if (detail.startsWith(StrApp.PREFIX_START_DATE)) return StrApp.PREFIX_START_DATE;
        if (detail.startsWith(StrApp.PREFIX_END_DATE))   return StrApp.PREFIX_END_DATE;
        if (detail.startsWith(StrApp.PREFIX_STATUS))     return StrApp.PREFIX_STATUS;
        if (detail.startsWith(StrApp.PREFIX_NAME))       return StrApp.PREFIX_NAME;
        if (detail.startsWith(StrApp.PREFIX_SURNAME))    return StrApp.PREFIX_SURNAME;
        if (detail.startsWith(StrApp.PREFIX_EMAIL))      return StrApp.PREFIX_EMAIL;
        if (detail.startsWith(StrApp.PREFIX_PHONE))      return StrApp.PREFIX_PHONE;
        if (detail.startsWith(StrApp.PREFIX_PICKUP))     return StrApp.PREFIX_PICKUP;
        return "";
    }

    // =================================================================================
    // GETTER & SETTER IN METHOD CHAINING (Fluent API)
    // =================================================================================

    public RentI getRental() { return rental; }

    public String getName() { return name; }
    public RentalFormBean setName(String name) {
        this.name = name;
        return this;
    }

    public String getSurname() { return surname; }
    public RentalFormBean setSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public String getEmail() { return email; }
    public RentalFormBean setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() { return phone; }
    public RentalFormBean setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPickupLocation() { return pickupLocation; }
    public RentalFormBean setPickupLocation(String loc) {
        this.pickupLocation = loc;
        return this;
    }

    public LocalDate getStart() { return start; }
    public RentalFormBean setStart(LocalDate start) {
        this.start = start;
        return this;
    }

    public LocalDate getEnd() { return end; }
    public RentalFormBean setEnd(LocalDate end) {
        this.end = end;
        return this;
    }

    public String getProductId() { return productId; }
    public RentalFormBean setProductId(String productId) {
        this.productId = productId;
        return this;
    }
    public String getOwnerEmail() { return ownerEmail; }
    public RentalFormBean setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        return this;
    }

    public Payment getPayment() {
        return new Payment();
    }
}