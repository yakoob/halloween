package koob.domain.utils

trait JsonSerializable implements Serializable {

    private static final long serialVersionUID = 1802465122L

    abstract public String getJsonTemplatePath()

}