import groovy.xml.*
import groovy.transform.*

baseDir = '../Blog/Posts'

def bloggerId = '6671019398434141469'
@Field def bloggerLabel = 'Ratpacked'

def baseURI = "https://www.blogger.com/feeds/$bloggerId/posts/default/-/$bloggerLabel"

def nextLink = baseURI

while (nextLink) {
    println "Getting posts from $nextLink"
    feed = new XmlSlurper().parse(nextLink)
    feed.entry.each {
        handleEntry(it)
    }
    nextLink = feed.link.find { it.@rel == 'next' }.@href.toString() - "/-/$bloggerLabel"
}

def handleEntry(entry) {
    def dir = createDirectory(entry.title)
    handleText entry.content, dir
    handleInfo entry, dir
}

def savePost(entry, dir) {
    def postFile = new File(dir, 'post.xml')
    postFile.text = entry
}

def handleInfo(entry, dir) {
    def link = entry.link.find { it.@rel == 'alternate' }.@href
    def infoFile = new File(dir, 'info.groovy')
    infoFile.delete()
    if (!infoFile.exists()) {
        infoFile << "url=$link"
        infoFile << "\n"
        infoFile << "title=$entry.title"
    }
}

def handleText(content, dir) {
    def pres = content.toString() =~ /(?ms)<pre class="brush:groovy.*">(.*?)<\/pre>/
    def sample = new File(dir, 'sample.groovy')
    if (!sample.exists()) {
        println "Saving sample code in $sample."
        pres.each {
            def first = true
            it[1].eachLine { line ->
                if (!(first && !line)) {
                    sample << line + '\n'
                }
                first = false
            }
        }
    }
}

def createDirectory(title) {
    def newTitle = title.toString() - "$bloggerLabel: "
    newTitle = newTitle.trim()
    newTitle = newTitle.replaceAll(/(,|'|"|\?|:|\/)/, '')
    newTitle = newTitle.replaceAll(/(\.)/, ' ')
    newTitle = newTitle - '('
    newTitle = newTitle - ')'
    if (newTitle.startsWith("the")) {
        newTitle = newTitle[0].toUpperCase() + newTitle[1..-1]
    }

    if (newTitle == 'String.multiply()') {
        newTitle = 'String multiply'
    }

    newTitle = newTitle.replaceAll(/\s+/, '_')
    def dir = new File(baseDir, newTitle)
    if (!dir.exists()) {
        println "Creating dir $dir.absolutePath"
        dir.mkdirs()
    }
    dir
}
