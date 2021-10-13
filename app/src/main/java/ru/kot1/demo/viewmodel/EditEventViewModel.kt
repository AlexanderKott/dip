package ru.kot1.demo.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.kot1.demo.auth.AppAuth
import ru.kot1.demo.dto.EventUI
import ru.kot1.demo.dto.emptyEvent
import ru.kot1.demo.enumeration.AttachmentType
import ru.kot1.demo.model.*
import ru.kot1.demo.repository.AppEntities
import ru.kot1.demo.repository.RecordOperation
import ru.kot1.demo.util.SingleLiveEvent
import ru.kot1.demo.work.SaveEventWorker
import javax.inject.Inject




@HiltViewModel
@ExperimentalCoroutinesApi
class EditEventViewModel @Inject constructor(
    var repository: AppEntities,
    var workManager: WorkManager,
    var auth: AppAuth
) : ViewModel() {

    private val edited = MutableLiveData(emptyEvent)

    private val _attach = MutableLiveData<PreparedData?>(null)
    val attach: LiveData<PreparedData?>
        get() = _attach

    private val _eventUI = ru.kot1.demo.model.SingleLiveEvent<EventUI>()
    val eventUI: ru.kot1.demo.model.SingleLiveEvent<EventUI>
        get() = _eventUI

    private val _eventText = MutableLiveData<String?>()
    val eventText: LiveData<String?>
        get() = _eventText

    private var operation = RecordOperation.NEW_RECORD

    fun save() {
        edited.value?.let { event ->
            viewModelScope.launch {
                try {
                    val type = _attach.value?.dataType?.let { it.toString() }
                    val uri = _attach.value?.uri?.let {
                        it.toString()
                    }
                    val id : Long =
                        repository.saveEventForWorker(
                            event.copy(
                                link = null,
                                datetime = 0,
                                type = null,
                                speakerIds = mutableListOf()

                            ),uri, type)

                    initWorkManager(id, operation)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        edited.value = emptyEvent
        _attach.value = null
        _eventText.value = null
    }

    fun loadEvent(id: Long) {
        viewModelScope.launch {
            operation = RecordOperation.CHANGE_RECORD
            val event = repository.getEventByIdFromDB(id).toDto()
            edited.value = event
            _eventText.value = event.content

            if (event.attachment != null) {
                _attach.value = PreparedData(null,
                    AttachmentType.valueOf(event.attachment.type))
            } else {
                _attach.value = null
            }
        }
    }


    fun deleteEvent(id: Long) {
        initWorkManager(id, RecordOperation.DELETE_RECORD)
    }

    private fun initWorkManager(id: Long, operation: RecordOperation) {
        val data = workDataOf(
            SaveEventWorker.postKey to arrayOf(operation.toString(),"$id")
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SaveEventWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(request)
    }


    fun prepareEventText(content: String) {
        edited.value = edited.value?.copy(content = content)
    }

    fun prepareTargetAttach(uri: Uri?, type: AttachmentType?) {
        _attach.value = PreparedData(uri, type)
    }


    fun prepareForNew() {
        operation = RecordOperation.NEW_RECORD
        edited.value = emptyEvent
        _attach.value = null
        _eventText.value = ""
    }



}




