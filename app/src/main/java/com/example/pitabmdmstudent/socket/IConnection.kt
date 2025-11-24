package com.example.pitabmdmstudent.socket

interface IConnection {
    fun initializeCommunication()   // Initializes the connection.
    fun connect()                   // Connects the socket.
    fun disconnect()                // Disconnects the socket.
    fun emit(event: String, data: Any) // Emits events to the server.
    fun on(event: String, callback: (args: Array<Any>) -> Unit) // Listens to events from the server.
}