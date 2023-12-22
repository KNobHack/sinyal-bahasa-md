package com.example.sinyal.dataclass

import com.google.gson.annotations.SerializedName

data class EventDetail(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("start_time")
	val startTime: Int? = null,

	@field:SerializedName("max_participant")
	val maxParticipant: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("end_time")
	val endTime: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lon")
	val lon: Any? = null,

	@field:SerializedName("thumbnail_url")
	val thumbnailUrl: String? = null,

	@field:SerializedName("lat")
	val lat: Any? = null
)
