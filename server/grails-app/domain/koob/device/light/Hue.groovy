package koob.device.light


import koob.visualization.ColorHue

class Hue extends Light {

    ColorHue color

    static constraints = {

    }

    static Hue getPortable(){
        return Hue.findByDescription("Portable")
    }

    @Override
    public String toString(){
        return "${this.class.simpleName} node:$node with state:$state <> $color"
    }
}
