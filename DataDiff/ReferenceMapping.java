public class ReferenceMapping {
    boolean active;
    String consumerType;
    String ct_code;
    String value;

    // constructor
    public ReferenceMapping(boolean active, String consumerType, String ct_code, String value) {
        this.active = active;
        this.consumerType = consumerType;
        this.ct_code = ct_code;
        this.value = value;
    }

    // getters
    public boolean getActive() { return active; }
    public String getConsumerType() { return consumerType; }
    public String get_ct_code() { return ct_code; }
    public String getValue() { return value; }

    // setters
    public void setActiveID(boolean activeID) { this.active = activeID; }
    public void setConsumerType(String consumerType) { this.consumerType = consumerType; } 
    public void set_ct_code(String ct_code) { this.ct_code = ct_code; }
    public void setValue(String value) { this.value = value; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof ReferenceMapping))
            return false;
        ReferenceMapping rm = (ReferenceMapping) obj;
        return rm.getActive() == this.getActive()
                && rm.getConsumerType() == this.getConsumerType()
                && rm.get_ct_code() == this.get_ct_code()
                && rm.getValue() == this.getValue();
    }

    @Override
    public int hashCode() {
        int result=31;
        result = 89 * result + (active ? 1 : 0);
        result = 89 * result + (consumerType !=null ? consumerType.hashCode() : 0);
        result = 89 * result + (ct_code !=null ? ct_code.hashCode() : 0);
        result += value.hashCode();
        return result;
    }

}