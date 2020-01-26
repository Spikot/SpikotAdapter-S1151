package kr.heartpattern.spikotadapter.s1151

import kr.heartpattern.spikot.adapters.NBTAdapter
import kr.heartpattern.spikot.nbt.*
import net.minecraft.server.v1_15_R1.*
import java.util.*

class WrapperNBTEndImpl : WrapperNBTEnd {
    override val tag: Any
        get() = NBTTagEnd.b

    override val value: Unit
        get() = Unit
}

class WrapperNBTByteImpl(override val tag: NBTTagByte) : WrapperNBTByte {
    constructor(value: Byte) : this(NBTTagByte.a(value))

    override val value: Byte
        get() = tag.asByte()
}

class WrapperNBTLongImpl(override val tag: NBTTagLong) : WrapperNBTLong {
    constructor(value: Long) : this(NBTTagLong.a(value))

    override val value: Long
        get() = tag.asLong()
}

class WrapperNBTIntImpl(override val tag: NBTTagInt) : WrapperNBTInt {
    constructor(value: Int) : this(NBTTagInt.a(value))

    override val value: Int
        get() = tag.asInt()
}

class WrapperNBTShortImpl(override val tag: NBTTagShort) : WrapperNBTShort {
    constructor(value: Short) : this(NBTTagShort.a(value))

    override val value: Short
        get() = tag.asShort()
}

class WrapperNBTDoubleImpl(override val tag: NBTTagDouble) : WrapperNBTDouble {
    constructor(value: Double) : this(NBTTagDouble.a(value))

    override val value: Double
        get() = tag.asDouble()
}

class WrapperNBTFloatImpl(override val tag: NBTTagFloat) : WrapperNBTFloat {
    constructor(value: Float) : this(NBTTagFloat.a(value))

    override val value: Float
        get() = tag.asFloat()
}

class WrapperNBTByteArrayImpl(override val tag: NBTTagByteArray) : WrapperNBTByteArray {
    constructor(value: ByteArray) : this(NBTTagByteArray(value))

    override val value: ByteArray
        get() = tag.bytes
}

class WrapperNBTIntArrayImpl(override val tag: NBTTagIntArray) : WrapperNBTIntArray {
    constructor(value: IntArray) : this(NBTTagIntArray(value))

    override val value: IntArray
        get() = tag.ints
}

class WrapperNBTLongArrayImpl(override val tag: NBTTagLongArray) : WrapperNBTLongArray {
    constructor(value: LongArray) : this(NBTTagLongArray(value))

    override val value: LongArray
        get() = tag.longs
}

class WrapperNBTStringImpl(override val tag: NBTTagString) : WrapperNBTString {
    constructor(value: String) : this(NBTTagString.a(value))

    override val value: String
        get() = tag.asString()
}

class WrapperNBTListImpl<W : WrapperNBTBase<*>>(override val tag: NBTTagList) : WrapperNBTList<W>,
    AbstractMutableList<W>() {
    constructor(value: List<W>) : this(NBTTagList()) {
        for (element in value) {
            tag.add(element.tag as NBTBase)
        }
    }

    override val enclosing: TagType<*>
        get() = TagType.ofId(tag.a_())

    override val value: MutableList<W>
        get() = this

    override val size: Int
        get() = tag.size

    override fun add(index: Int, element: W) {
        tag.add(index, element.tag as NBTBase)
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): W {
        return NBTAdapter.wrapNBT(tag.i(index)) as W
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAt(index: Int): W {
        return NBTAdapter.wrapNBT(tag.removeAt(index)) as W
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, element: W): W {
        val old = NBTAdapter.wrapNBT(tag.i(index)) as W
        tag.a(index, element.tag as NBTBase)
        return old
    }
}

class WrapperNBTCompoundImpl(override val tag: NBTTagCompound) : WrapperNBTCompound,
    AbstractMutableMap<String, WrapperNBTBase<*>>() {
    private companion object {
        val mapField = NBTTagCompound::class.java.getDeclaredField("map")!!

        init {
            mapField.isAccessible = true
        }
    }

    constructor(map: Map<String, WrapperNBTBase<*>>) : this(NBTTagCompound()) {
        for ((key, value) in map) {
            tag.set(key, value.tag as NBTBase)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private val map = mapField.get(tag) as MutableMap<String, NBTBase>

    override val entries: MutableSet<MutableMap.MutableEntry<String, WrapperNBTBase<*>>>
        get() = object : AbstractMutableSet<MutableMap.MutableEntry<String, WrapperNBTBase<*>>>() {
            override val size: Int
                get() = map.size

            override fun add(element: MutableMap.MutableEntry<String, WrapperNBTBase<*>>): Boolean {
                if (element.key in map && map[element.key] == element.value.tag) return false
                map[element.key] = element.value.tag as NBTBase
                return true
            }

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, WrapperNBTBase<*>>> {
                return object : MutableIterator<MutableMap.MutableEntry<String, WrapperNBTBase<*>>> {
                    private val original = map.iterator()
                    override fun hasNext(): Boolean {
                        return original.hasNext()
                    }

                    override fun next(): MutableMap.MutableEntry<String, WrapperNBTBase<*>> {
                        return original.next().let { SimpleEntry(it.key, NBTAdapter.wrapNBT(it.value)) }
                    }

                    override fun remove() {
                        original.remove()
                    }
                }
            }
        }

    override fun clear() {
        map.clear()
    }

    override fun containsKey(key: String): Boolean {
        return tag.hasKey(key)
    }

    override fun containsValue(value: WrapperNBTBase<*>): Boolean {
        return map.containsValue(value.tag as NBTBase)
    }

    override fun get(key: String): WrapperNBTBase<*>? {
        return NBTAdapter.wrapNBT(tag[key]!!)
    }

    override fun isEmpty(): Boolean {
        return tag.isEmpty
    }

    override fun remove(key: String): WrapperNBTBase<*>? {
        return map.remove(key)?.let { NBTAdapter.wrapNBT(it) }
    }

    override fun remove(key: String, value: WrapperNBTBase<*>): Boolean {
        return map.remove(key, value.tag as NBTBase)
    }

    override val value: MutableMap<String, WrapperNBTBase<*>>
        get() = this

    override fun getBoolean(key: String): Boolean {
        return tag.getBoolean(key)
    }

    override fun getByte(key: String): Byte {
        return tag.getByte(key)
    }

    override fun getByteArray(key: String): ByteArray {
        return tag.getByteArray(key)
    }

    override fun getCompound(key: String): WrapperNBTCompound {
        return NBTAdapter.wrapNBTCompound(tag.getCompound(key))
    }

    override fun getDouble(key: String): Double {
        return tag.getDouble(key)
    }

    override fun getEnd(key: String) {
        // Nothing
    }

    override fun getFloat(key: String): Float {
        return tag.getFloat(key)
    }

    override fun getInt(key: String): Int {
        return tag.getInt(key)
    }

    override fun getIntArray(key: String): IntArray {
        return tag.getIntArray(key)
    }

    override fun <W : WrapperNBTBase<*>> getList(key: String, type: TagType<*>): WrapperNBTList<W> {
        return NBTAdapter.wrapNBTList(tag.getList(key, type.id))
    }

    override fun getLong(key: String): Long {
        return tag.getLong(key)
    }

    override fun getLongArray(key: String): LongArray {
        return NBTAdapter.wrapNBTLongArray(tag[key]!!).value
    }

    override fun getShort(key: String): Short {
        return tag.getShort(key)
    }

    override fun getString(key: String): String {
        return tag.getString(key)
    }

    override fun getUUID(key: String): UUID {
        return tag.a(key)!!
    }

    override fun hasKeyOfType(key: String, type: TagType<*>): Boolean {
        return tag.hasKeyOfType(key, type.id)
    }

    override fun hasUUID(key: String): Boolean {
        return tag.b(key)
    }

    override fun put(key: String, value: WrapperNBTBase<*>): WrapperNBTBase<*>? {
        val old = tag.get(key)?.let { NBTAdapter.wrapNBT(it) }
        tag.set(key, value.tag as NBTBase)
        return old
    }

    override fun setBoolean(key: String, value: Boolean) {
        tag.setBoolean(key, value)
    }

    override fun setByte(key: String, value: Byte) {
        tag.setByte(key, value)
    }

    override fun setByteArray(key: String, value: ByteArray) {
        tag.setByteArray(key, value)
    }

    override fun setCompound(key: String, value: WrapperNBTCompound) {
        tag.set(key, value.tag as NBTBase)
    }

    override fun setDouble(key: String, value: Double) {
        tag.setDouble(key, value)
    }

    override fun setEnd(key: String, value: Unit) {
        tag.set(key, WrapperNBTEndImpl().tag as NBTBase)
    }

    override fun setFloat(key: String, value: Float) {
        tag.setFloat(key, value)
    }

    override fun setInt(key: String, value: Int) {
        tag.setInt(key, value)
    }

    override fun setIntArray(key: String, value: IntArray) {
        tag.setIntArray(key, value)
    }

    override fun <W : WrapperNBTBase<*>> setList(key: String, value: WrapperNBTList<W>) {
        tag.set(key, value.tag as NBTBase)
    }

    override fun setLong(key: String, value: Long) {
        tag.setLong(key, value)
    }

    override fun setLongArray(key: String, value: LongArray) {
        tag.set(key, NBTAdapter.createNBTLongArray(value).tag as NBTBase)
    }

    override fun setShort(key: String, value: Short) {
        tag.setShort(key, value)
    }

    override fun setString(key: String, value: String) {
        tag.setString(key, value)
    }

    override fun setUUID(key: String, value: UUID) {
        tag.a(key, value)
    }
}