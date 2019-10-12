package koob.server

import grails.gorm.transactions.Transactional

import javax.annotation.PostConstruct

@Transactional
class ServerService {

    private Server thisServer

    @PostConstruct
    Server init(){
        thisServer = new Server(name: InetAddress?.getLocalHost()?.getHostName()?.tokenize(".")?.first(), ipAddress: InetAddress?.getLocalHost()?.getHostAddress()).save()
    }

    public Server getThisServer(){
        return thisServer
    }

}

