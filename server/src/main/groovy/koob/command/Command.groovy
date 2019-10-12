package koob.command

import groovy.transform.AutoClone

@AutoClone
class Command implements Serializable {

    public String getName(){
        this.class.simpleName
    }

    def payload

}
