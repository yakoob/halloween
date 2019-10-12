package koob.domain.visualization

import grails.converters.JSON
import groovy.transform.TupleConstructor
import koob.domain.utils.JsonSerializable

@TupleConstructor
class HueEffect implements JsonSerializable {

    boolean on = true
    int bri = 255
    int sat = 255
    int hue = 30000

    static HueEffect getPurple(){
        new HueEffect(on: true, hue: 50000)
    }

    static HueEffect getBlue(){
        new HueEffect(on: true, hue: 40000)
    }

    static HueEffect getGreen(){
        new HueEffect(on: true, hue: 30000)
    }

    static HueEffect getOrange(){
        new HueEffect(on: true, hue: 10000)
    }

    static HueEffect getRed(){
        new HueEffect(on: true, hue: 60000)
    }

    static HueEffect getWhite(){
        new HueEffect(on: true, sat: 0)
    }

    static HueEffect On(){
        new HueEffect(on:true)
    }

    static HueEffect Off(){
        new HueEffect(on:false)
    }

    HueEffect on(){
        this.on = true
        this
    }

    HueEffect off(){
        this.on = false
        this
    }

    @Override
    public String getJsonTemplatePath(){
        return "/lighting/_hueEffect"
    }
}
