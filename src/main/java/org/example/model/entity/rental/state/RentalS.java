package org.example.model.entity.rental.state;

import org.example.model.entity.actors.User;
import org.example.model.entity.rental.Payment;
import org.example.model.entity.rental.StatusEnum;
import org.example.model.services.rent.ActionsOwnerRentI;
import org.example.model.services.rent.RentI;
import java.time.LocalDate;
import java.util.Objects;

public class RentalS implements RentI,RentalContext {

    private String clientEmail;
    private String clientPhone; 
    private String ownerEmail;
    private String productId;
    private String locate;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // 🆕 Il pattern State sostituisce il semplice Enum. Di default parte in Pending.
    private RentalState state = new PendingState();

    // Il pagamento potrebbe essere un ID o un oggetto "Value Object" semplice
    private Payment payment;

    public RentalS() {}

    // Costruttore pulito aggiornato
    public RentalS(String clientEmail, String clientPhone, String productId, String locate, LocalDate start, LocalDate end) {
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.productId = productId;
        this.startDate = start;
        this.endDate = end;
        this.locate = locate;
    }

    public RentalS(String clientEmail, String ownerEmail, String productId) {
        this.clientEmail = clientEmail;
        this.ownerEmail = ownerEmail;
        this.productId = productId;
        this.startDate = LocalDate.now();
    }

    /**
     * Spara la notifica verso il proxy dell'ObserverDAO.
     * Funziona perché RentI estende EntityI, che fornisce notifyObservers().
     */
    public void commitChange() {
        this.notifyObservers(this);
    }

    /**
     * 🆕 Metodo a visibilità di pacchetto utilizzato dagli stati concreti 
     * per far avanzare lo stato del noleggio, attivando l'auto-persistenza.
     */
    public void setInternalState(RentalState state) {
        this.state = state;
        this.commitChange();
    }

    // =========================================================================
    // 🆕 AZIONI DI BUSINESS DELEGATE ALLO STATO (Fluent API)
    // =========================================================================

    public void approve() {
        this.state.approve(this);

    }

    public void reject() {
        this.state.reject(this);

    }

    public void activate() {
        this.state.activate(this);

    }

    public void complete() {
        this.state.complete(this);

    }

    public void cancel() {
        this.state.cancel(this);

    }

    /**
     * Verifica se il noleggio può essere annullato nello stato attuale.
     */
    public boolean canBeCancelled() {
        return this.state.canBeCancelled();
    }

    // =========================================================================
    // GETTERS & SETTERS IN METHOD CHAINING (Fluent API) con Auto-Persistenza
    // =========================================================================

    public String getClientEmail() {
        return clientEmail;
    }

    public RentalS setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
        this.commitChange(); 
        return this;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public RentalS setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
        this.commitChange();
        return this;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public RentalS setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
        this.commitChange();
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public RentalS setProductId(String productId) {
        this.productId = productId;
        this.commitChange();
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public RentalS setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.commitChange();
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.commitChange();
    }

    public String getLocate() {
        return locate;
    }

    public RentalS setLocate(String locate) {
        this.locate = locate;
        this.commitChange();
        return this;
    }

    /**
     * 🆕 Restituisce l'enum corrispondente chiedendolo direttamente allo stato attuale.
     */
    public StatusEnum getStatus() {
        return this.state.getStatusEnum();
    }

    @Override
    public boolean isOwner(ActionsOwnerRentI actionsOwnerRentI) {//todo
        if(actionsOwnerRentI instanceof User user)
         return Objects.equals(getOwnerEmail(), user.getEmail());
        throw new IllegalArgumentException("isOwner SOLO se user e valutabile");

    }

    /**
     * 🆕 Utile principalmente per la ricostruzione dell'oggetto da Database/DAO.
     * Mappa l'enum salvato sul database nella rispettiva classe di stato concreta.
     */
    public RentalS setStatus(StatusEnum status) {
        if (status == null) {
            this.state = new PendingState();
            return this;
        }
        this.state = switch (status) {
            case PENDING -> new PendingState();
            case APPROVED -> new ApprovedState();
            case ACTIVE -> new ActiveState();
            case COMPLETED -> new CompletedState();
            case CANCELLED -> new CancelledState();
            case REJECTED -> new RejectedState();
        };
        this.commitChange();
        return this;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        this.commitChange();
    }

    // =========================================================================
    // UTILITY PER AGGIORNAMENTO DI GRUPPO (Fluent anch'esso)
    // =========================================================================
    public RentalS updateFields(String clientEmail, String clientPhone, String productId, String locate, LocalDate startDate, LocalDate endDate) {
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.productId = productId;
        this.locate = locate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.commitChange();
        return this;
    }

    // ========================================
    // IDENTIFICATIVO UNICO (Ereditato via RentI -> EntityI)
    // ========================================
    @Override
    public String getId() {
        return clientEmail + "I" + productId;
    }
}