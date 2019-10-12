package koob.visualization

import koob.domain.visualization.HueEffect

class ColorHue extends Color {

    public String getRGB(){
        return "$red,$green,$blue"
    }


    public HueEffect getHue(){
        HueEffect hueEffect

        if (description ==Color.Name.PURPLE)
            hueEffect = new HueEffect(on: true, hue: 50000)

        else if (description==Color.Name.BLUE)
            hueEffect = new HueEffect(on: true, hue: 40000)

        else if (description==Color.Name.GREEN)
            hueEffect = new HueEffect(on: true, hue: 30000)

        else if (description==Color.Name.ORANGE)
            hueEffect = new HueEffect(on: true, hue: 10000)

        else if (description==Color.Name.RED)
            hueEffect = new HueEffect(on: true, hue: 60000)

        else if (description==Color.Name.WHITE)
            hueEffect = new HueEffect(on: true, sat: 0)

        return hueEffect
        /*
            {"on":true,"bri":255, "effect":"colorloop"}
            http://192.168.20.153/api/23e3d7c3116a922f3c8b60bb2ce27da7/lights/4/state
         */
    }

    static constraints = {
    }
}
