package xyz.gigashi.chatworkglancer

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.awaitResponseResult
import com.github.kittinunf.fuel.json.jsonDeserializer

class ChatworkApi private constructor() {
    companion object {

        // http://developer.chatwork.com/ja/endpoint_me.html#GET-me
        suspend fun me(token: String) =
            Fuel.get("https://api.chatwork.com/v2/me")
                .header("X-ChatWorkToken", token)
                .awaitResponseResult(jsonDeserializer())
                .third

        // http://developer.chatwork.com/ja/endpoint_rooms.html#GET-rooms
        suspend fun rooms(token: String) =
            Fuel.get("https://api.chatwork.com/v2/rooms")
                .header("X-ChatWorkToken", token)
                .awaitResponseResult(jsonDeserializer())
                .third

        // http://developer.chatwork.com/ja/endpoint_rooms.html#GET-rooms-room_id-messages
        suspend fun messages(token: String, roomId: String) =
            Fuel.get("https://api.chatwork.com/v2/rooms/$roomId/messages")
                .header("X-ChatWorkToken", token)
                .awaitResponseResult(jsonDeserializer())
                .third
    }
}