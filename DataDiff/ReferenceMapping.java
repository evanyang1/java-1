public class ReferenceMapping {
    String activeID;
    String consumerType;
    String ct_code;
    int value;

    // getters
    public String getActiveID() { return activeID; }
    public String getConsumerType() { return consumerType; }
    public String get_ct_code() { return ct_code; }
    public int getValue() { return value; }

    // setters
    public void setActiveID(String activeID) { this.activeID = activeID; }
    public void setConsumerType(String consumerType) { this.consumerType = consumerType; } 
    public void set_ct_code(String ct_code) { this.ct_code = ct_code; }
    public void setValue(int value) { this.value = value; }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof ReferenceMapping))
            return false;
        ReferenceMapping rm = (ReferenceMapping) obj;
        return rm.getActiveID() == this.getActiveID()
                && rm.getConsumerType() == this.getConsumerType()
                && rm.get_ct_code() == this.get_ct_code()
                && rm.getValue() == this.getValue();
    }

    @Override
    public int hashCode() {
        int result=31;
        result = 89 * result + (activeID !=null ? activeID.hashCode() : 0);
        result = 89 * result + (consumerType !=null ? consumerType.hashCode() : 0);
        result = 89 * result + (ct_code !=null ? ct_code.hashCode() : 0);
        result += value;
        return result;
    }

}