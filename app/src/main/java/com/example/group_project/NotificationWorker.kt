package com.example.group_project

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.coroutineScope
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class NotificationWorker (context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private var context: Context

    var timer = Timer("schedule", true)

    var counter: Int = 0

    init {
        this.context = context
    }

    override suspend fun doWork(): Result = coroutineScope {

        Log.d("Notification--------------------->", "Inside DO Work")
        val ref = FirebaseDatabase.getInstance().getReference("events")

        val initialList = ArrayList<Event>()
        val finalList = ArrayList<Event>()

        val job1 = async {

            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if (p0!!.exists())
                    {
                        for(event in p0.children)
                        {
                            val event = event.getValue(Event::class.java)
                            initialList.add(event!!)
                        }
                    }
                }

            })

        }

        job1.await()

        val job3 = async {

            timer.schedule(60000) {

                //Do Nothing
            }

        }

        job3.await()

        val job2 = async {

            Log.d("Notification---------------------------->", "Started Job 2")

            ref.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot?) {

                    if (p0!!.exists())
                    {
                        for(event in p0.children)
                        {
                            val event = event.getValue(Event::class.java)
                            finalList.add(event!!)
                        }
                    }
                }

            })

        }

        // awaitAll will throw an exception if a download fails, which CoroutineWorker will treat as a failure
        job2.await()




        Log.d("Notification---------------------------->", "initial size " + initialList.size.toString())
        Log.d("Notification---------------------------->", "final size " + finalList.size.toString())
        if (initialList.size < finalList.size)
        {
            var builder = NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.places_ic_search)
                .setContentTitle("New Events Availabale!")
                .setContentText("Click to view new events..!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }

        }

        Result.success()
    }


}