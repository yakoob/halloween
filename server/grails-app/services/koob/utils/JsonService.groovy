package koob.utils

import grails.gorm.transactions.Transactional
import grails.plugin.json.view.JsonViewTemplateEngine
import koob.domain.utils.JsonSerializable
import groovy.text.Template
import org.springframework.beans.factory.annotation.Autowired

@Transactional
class JsonService {

    @Autowired
    JsonViewTemplateEngine jsonViewTemplateEngine

    def toJsonFromDomainTemplate(JsonSerializable obj) {
        Template t = jsonViewTemplateEngine.resolveTemplate(obj.jsonTemplatePath)
        def writable = t.make(obj: obj)
        def sw = new StringWriter()
        writable.writeTo( sw )
        return sw.toString()
    }

}
