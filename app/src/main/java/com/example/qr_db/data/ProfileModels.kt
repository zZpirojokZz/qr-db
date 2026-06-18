package com.example.qr_db.data

import com.google.gson.annotations.SerializedName

data class ContactPerson(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("sub_role") val subRole: String? = null
)

data class TeacherProfileResponse(
    @SerializedName("teacher") val teacher: User,
    @SerializedName("curated_group") val curatedGroup: String? = null,
    @SerializedName("group_leader") val groupLeader: ContactPerson? = null,
    @SerializedName("department_head") val departmentHead: ContactPerson? = null
)

data class StudentProfileResponse(
    @SerializedName("student") val student: User,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("group_name") val groupName: String? = null,
    @SerializedName("curator") val curator: ContactPerson? = null,
    @SerializedName("group_leader") val groupLeader: ContactPerson? = null,
    @SerializedName("department_head") val departmentHead: ContactPerson? = null,
    @SerializedName("is_group_leader") val isGroupLeader: Boolean = false
)