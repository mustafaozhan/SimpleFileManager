package mustafaozhan.github.com.simplefilemanager.model

 class Item(val name: String?, val data: String, val date: String, val path: String, val image: String) : Comparable<Item> {

    override fun compareTo(other: Item): Int {
        if (this.name != null)
            return this.name.toLowerCase().compareTo(other.name!!.toLowerCase())
        else
            throw IllegalArgumentException()
    }
}
