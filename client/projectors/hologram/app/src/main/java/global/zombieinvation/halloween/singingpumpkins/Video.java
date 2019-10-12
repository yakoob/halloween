package global.zombieinvation.halloween.singingpumpkins;

/**
 * Created by yakoobahmad on 10/16/16.
 */
class Video {



    enum Name {
        WAITING,
        SAM_NOCOSTUME,SAM_SYMPHONY,SAM_SCARE1,SAM_SCARE2,SAM_SCARE3,SAM_SCARE4,
        BONEYARD_BAND,BONEYARD_PUMPKIN,
        OMINOUS_OCULI_SWARM,DRAGON_FULL,DRAGON_EYE,
        SPECTRAL_SURFACES,SPECTRAL_SCARE1,SPECTRAL_SCARE2,SPECTRAL_SCARE3,
        MONSTERS_BAND,MONSTERS_DANCE,MONSTERS_SCAREY,MONSTERS_SILY,
        JESTER_BALLOON,JESTER_CLOWNING,JESTER_JOYRIDE,JESTER_MECHANICAL,JESTER_NOWYOUSEEME,JESTER_SCARES,
        MOON,ZOMBIE_TWIST,WALL_BANGERS,YOU_AXED,BOO
    }

    private Long id;
    private Name name;
    private String command;
    private String event;
    private Boolean next;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Name getName(){
        return this.name;
    }

    public void setName(Name n){
        this.name = n;
    }

    public String getCommand(){
        return this.command;
    }
    public void setCommand(String c){
        this.command = c;
    }

    public String getEvent(){ return this.event; }
    public void setEvent(String e) { this.event = e; }

    public Boolean getNext(){ return this.next; }
    public void setNext(Boolean n){
        this.next = n;
    }
}
