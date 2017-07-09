package com.pkmmte.pkrss

import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * Created on July 8, 2017
 *
 * @author Pkmmte Xeleon
 */
class AdaptiveReference<T>(value: T, private val type: Type) {
	enum class Type { WEAK, SOFT, STRONG }

	// Reference values
	private val weak: WeakReference<T>?
	private val soft: SoftReference<T>?
	private val strong: T?

	init {
		when (type) {
			Type.WEAK -> {
				weak = WeakReference(value)
				soft = null
				strong = null
			}
			Type.SOFT -> {
				weak = null
				soft = SoftReference(value)
				strong = null
			}
			Type.STRONG -> {
				weak = null
				soft = null
				strong = value
			}
		}
	}

	fun get(): T? {
		return when (type) {
			Type.WEAK -> weak?.get()
			Type.SOFT -> soft?.get()
			Type.STRONG -> strong
		}
	}
}
