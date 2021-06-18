package org.application.birthday_notification.model

// id에는 카카오톡에서 보내는 uuid 사용
data class User(var id: Int, var name: String, var birthday: String, var alarmSet: Boolean = true) {
}