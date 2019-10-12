package global.zombieinvation.halloween.singingpumpkins;

/**
 * Created by yakoobahmad on 10/16/16.
 */
class Video {

    enum Name {
        WOODS,
        ADDAMS_FAMILY,
        BLUE_MOON,
        DAY_O,
        DEAD_MANS_PARTY,
        GHOST_HOST,
        GHOST_BUSTERS,
        GRIM_GRINNING_GHOST,
        JUMP_IN_THE_LINE,
        MONSTER_MASH,
        NIGHTMARE_ON_MY_STREET,
        RED_RIDING_HOOD,
        SEXY_AND_I_KNOW_IT,
        SOMEBODYS_WATCHING_ME,
        THEYRE_COMING,
        THIS_IS_HALLOWEEN,
        THRILLER,
        TIME_WARP,
        WEREWOLVES_IN_LONDON,
        YO_HO_HO,
        ZOMBIE_JAMBORE,
        BOHEMIAN_RHAPSODY,
        JOKE_COBWEBS,
        JOKE_DEADICATION,
        JOKE_EATING_CLOWN,
        JOKE_HOLLYWOOD_GHOST,
        JOKE_KNOCK_KNOCK,
        JOKE_LOOK_BOTH_WAYS,
        JOKE_MUMMY,
        JOKE_NOTHING_BUT_BEST,
        JOKE_PUMPKIN_PATCH,
        JOKE_STAIRING_AT,
        JOKE_TASTEFULL,
        JOKE_TOO_MUCH_CANDY,
        JOKE_UNDER_SKIN,
        JOKE_ZOMBIE_EYES
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

    public Boolean getNext(){ return this.next; }
    public void setNext(Boolean n){ this.next = n; }

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

}
