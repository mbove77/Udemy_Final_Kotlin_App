package com.bove.martin.udemyfinalapp.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Martín Bove on 03/10/2019.
 * E-mail: mbove77@gmail.com
 */

object RxBus {
    private  val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> listen(eventType: Class<T>): Observable<T> {
        return publisher.ofType(eventType)
    }

}