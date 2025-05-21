package com.example.personaltasks.model

import android.os.Parcelable
import com.example.personaltasks.model.Constant.INVALID_TASK_ID
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var id: Int? = INVALID_TASK_ID,
    var title: String = "",
    var description: String = "",
    var deadline: String = "",
    var details: String = ""
) : Parcelable