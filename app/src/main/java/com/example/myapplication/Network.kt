package com.example.myapplication

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson

object Network {

    var base_url = "http://150.241.123.161:8080"
    val signUp_url = "${base_url}/signUp"
    val signIn_url = "${base_url}/signIn"
    val logs_url = "${base_url}/logs"
    val condition_url = "${base_url}/condition"
    val command_url = "${base_url}/command"
    //выключить все
    val comm0_url = "${command_url}/0"
    //зажечь первые 6 светодиодов
    val comm1_url = "${command_url}/1"
    //зажечь вторые 6 светодиодов
    val comm2_url = "${command_url}/2"
    //зажечь последние 4 светодиода
    val comm3_url = "${command_url}/3"
    //включить все
    val comm4_url = "${command_url}/4"

    val httpClient = HttpClient(OkHttp){
        install(ContentNegotiation){
            gson()
        }
    }



}
