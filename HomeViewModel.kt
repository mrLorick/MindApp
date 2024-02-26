package com.mindbyromanzanoni.viewModel

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.databinding.ObservableField
import androidx.media3.session.MediaController
import com.google.gson.Gson
import com.mindbyromanzanoni.base.BaseViewModel
import com.mindbyromanzanoni.data.repository.HomeDataSourceImp
import com.mindbyromanzanoni.data.response.CommonResponse
import com.mindbyromanzanoni.data.response.chatUsers.ChatUsersResponse
import com.mindbyromanzanoni.data.response.edification.EdificationCatResponse
import com.mindbyromanzanoni.data.response.edification.EdificationTypeResponse
import com.mindbyromanzanoni.data.response.eventDetails.AddCommentResponse
import com.mindbyromanzanoni.data.response.eventDetails.CommentResponse
import com.mindbyromanzanoni.data.response.eventDetails.EventDetailsResponse
import com.mindbyromanzanoni.data.response.eventDetails.LikesResponse
import com.mindbyromanzanoni.data.response.home.EventResponse
import com.mindbyromanzanoni.data.response.journal.JournalResponse
import com.mindbyromanzanoni.data.response.journal.ViewJournalsResponse
import com.mindbyromanzanoni.data.response.meditation.MeditationCatResponse
import com.mindbyromanzanoni.data.response.meditation.MeditationTypeListResponse
import com.mindbyromanzanoni.data.response.meditation.MeditationTypeResponse
import com.mindbyromanzanoni.data.response.notification.NotificationResponse
import com.mindbyromanzanoni.data.response.notificationStatus.NotificationStatusResponse
import com.mindbyromanzanoni.data.response.resource.ResourceCategoryResponse
import com.mindbyromanzanoni.data.response.resource.ResourceTypeList
import com.mindbyromanzanoni.data.response.resource.ResourceTypeResponse
import com.mindbyromanzanoni.data.response.search.SearchCatOrSubCatListResponse
import com.mindbyromanzanoni.data.response.search.SearchCatOrSubCatResponse
import com.mindbyromanzanoni.data.response.userData.UserDataResponse
import com.mindbyromanzanoni.data.response.userMessageList.MessageListResponse
import com.mindbyromanzanoni.data.response.userMessageList.MessageResponse
import com.mindbyromanzanoni.data.response.userMessageList.UserMessageResponse
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.socket.SignalRManager
import com.mindbyromanzanoni.utils.constant.ApiConstants
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.createPartFromJsonString
import com.mindbyromanzanoni.videoOrAudioControls.MediaControllerImpl
import com.mindbyromanzanoni.videoOrAudioControls.MediaControllerInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val authDataSourceImp: HomeDataSourceImp) :
    BaseViewModel() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var email = ObservableField("")
    var subject = ObservableField("")
    var message = ObservableField("")
    var image: File? = null
    var password = ObservableField("")
    var oldPassword = ObservableField("")
    var name = ObservableField("")
    var categoryId = ObservableField("")
    var confirmPassword = ObservableField("")
    var notes = ObservableField("")
    var entryTypeId = ObservableField("")
    var journalID = ObservableField("")
    var meditationSearchText = ObservableField("")
    var date = ObservableField("")
    var isNotificationOn = ObservableField(true)
    var isBioMatrixOn = ObservableField(true)
    var eventId = ObservableField("")
    var type = ObservableField(1)
    var commentDesc = ObservableField("")
    var isFavourite = ObservableField(false)
    var userId = ObservableField("")
    var resourceTypeId = ObservableField("")
    private var userName = ObservableField("-1")
    var otherUSerId = ObservableField(0)
    var notificationReminderDate = ObservableField("")
    var notificationTypeID = ObservableField(0)
    var typeId = ObservableField(0)
    var searchKeyword = ObservableField("")
    var videoUrl = ObservableField("")


    var mediaController: MediaControllerInterface? = null
    private var mainAudioUrl: String? = null
    var meditationDetails : MeditationTypeListResponse? = null
    var resourceDetails : ResourceTypeList? = null
    val musicStart = ObservableField("00:00")
    val musicEnd = ObservableField("00:00")
    val messageText = ObservableField("")
    val jsonObjectAddJournalWeight = JSONObject()
    private var signalRManager : SignalRManager? = null

    private val contactUsResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val contactUsSharedFlow = contactUsResponse.asSharedFlow()

    private val changePasswordResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val changePasswordSharedFlow = changePasswordResponse.asSharedFlow()

    private val meditationTypeListResponse: MutableSharedFlow<Resource<MeditationTypeResponse>> = MutableSharedFlow()
    val meditationTypeListSharedFlow = meditationTypeListResponse.asSharedFlow()

    private val meditationCatListResponse: MutableSharedFlow<Resource<MeditationCatResponse>> = MutableSharedFlow()
    val meditationCatListSharedFlow = meditationCatListResponse.asSharedFlow()

    private val searchCatOrSubCatResponse: MutableSharedFlow<Resource<SearchCatOrSubCatResponse>> = MutableSharedFlow()
    val searchCatOrSubCatSharedFlow = searchCatOrSubCatResponse.asSharedFlow()

    private val edificationListResponse: MutableSharedFlow<Resource<EdificationTypeResponse>> = MutableSharedFlow()
    val edificationListSharedFlow = edificationListResponse.asSharedFlow()

    private val edificationCategoryListResponse: MutableSharedFlow<Resource<EdificationCatResponse>> = MutableSharedFlow()
    val edificationCategoryListSharedFlow = edificationCategoryListResponse.asSharedFlow()

    private val journalListResponse: MutableSharedFlow<Resource<JournalResponse>> = MutableSharedFlow()
    val journalListSharedFlow = journalListResponse.asSharedFlow()

    private val notificationListResponse: MutableSharedFlow<Resource<NotificationResponse>> = MutableSharedFlow()
    val notificationListSharedFlow = notificationListResponse.asSharedFlow()

    private val addJournalResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val addJournalSharedFlow = addJournalResponse.asSharedFlow()

    private val resourceListResponse: MutableSharedFlow<Resource<ResourceCategoryResponse>> = MutableSharedFlow()
    val resourceListSharedFlow = resourceListResponse.asSharedFlow()

    private val eventListResponse: MutableSharedFlow<Resource<EventResponse>> = MutableSharedFlow()
    val eventListSharedFlow = eventListResponse.asSharedFlow()

    private val searchMeditationResponse: MutableSharedFlow<Resource<MeditationCatResponse>> = MutableSharedFlow()
    val searchMeditationSharedFlow = searchMeditationResponse.asSharedFlow()

    private val updateNotificationSettingResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val updateNotificationSettingSharedFlow = updateNotificationSettingResponse.asSharedFlow()

    private val commentListResponse: MutableSharedFlow<Resource<CommentResponse>> = MutableSharedFlow()
    val commentListSharedFlow = commentListResponse.asSharedFlow()

    private val likeListResponse: MutableSharedFlow<Resource<LikesResponse>> = MutableSharedFlow()
    val likeListSharedFlow = likeListResponse.asSharedFlow()

    private val eventDetailsResponse: MutableSharedFlow<Resource<EventDetailsResponse>> = MutableSharedFlow()
    val eventDetailsSharedFlow = eventDetailsResponse.asSharedFlow()

    private val updateJournalResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val updateJournalSharedFlow = updateJournalResponse.asSharedFlow()

    private val addCommentResponse: MutableSharedFlow<Resource<AddCommentResponse>> = MutableSharedFlow()
    val addCommentSharedFlow = addCommentResponse.asSharedFlow()

    private val favouriteStatusEventResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val favouriteEventStatusSharedFlow = favouriteStatusEventResponse.asSharedFlow()

    private val userProfileResponse: MutableSharedFlow<Resource<UserDataResponse>> = MutableSharedFlow()
    val userProfileSharedFlow = userProfileResponse.asSharedFlow()

    private val logoutResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val logoutSharedFlow = logoutResponse.asSharedFlow()

    private val journalDetailResponse: MutableSharedFlow<Resource<ViewJournalsResponse>> = MutableSharedFlow()
    val journalDetailSharedFlow = journalDetailResponse.asSharedFlow()

    private val resourceListByTypeResponse: MutableSharedFlow<Resource<ResourceTypeResponse>> = MutableSharedFlow()
    val resourceListByTypeSharedFlow = resourceListByTypeResponse.asSharedFlow()


    private val deleteJournalResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val deleteJournalSharedFlow = deleteJournalResponse.asSharedFlow()


    private val notificationStatusResponse: MutableSharedFlow<Resource<NotificationStatusResponse>> = MutableSharedFlow()
    val notificationStatusSharedFlow = notificationStatusResponse.asSharedFlow()

    private val chatUsersResponse: MutableSharedFlow<Resource<ChatUsersResponse>> = MutableSharedFlow()
    val chatUsersSharedFlow = chatUsersResponse.asSharedFlow()

    private val chatUsersListResponse: MutableSharedFlow<UserMessageResponse> = MutableSharedFlow()
    val chatUsersListSharedFlow = chatUsersListResponse.asSharedFlow()

    private val messageListResponse: MutableSharedFlow<MessageResponse> = MutableSharedFlow()
    val messageListSharedFlow = messageListResponse.asSharedFlow()

    private val sendMessageResponse: MutableSharedFlow<MessageListResponse> = MutableSharedFlow()
    val sendMessageSharedFlow = sendMessageResponse.asSharedFlow()

    private val notificationReminderResponse: MutableSharedFlow<Resource<CommonResponse>> = MutableSharedFlow()
    val notificationReminderResponseSharedPrefs = notificationReminderResponse.asSharedFlow()


    /**
     * Initialize media controller instance
     * @param context
     * @param controller callback of media controller
     * @param addMediaData callback for adding media data
     * */
    private fun initializeMediaController(
        context: Context,
        controller: (mediaController: MediaController?) -> Unit,
        addMediaData: () -> Unit
    ) {
        if (mediaController == null) {
            mediaController = mediaControllerInstance()
        }
        mediaController?.initializeMediaController(
            context = context,
            controller = { musicMediaController ->
                controller.invoke(musicMediaController)
            },
            addMediaData = {
                addMediaData.invoke()
            }
        )
    }

    private fun mediaControllerInstance(): MediaControllerInterface {
        return MediaControllerImpl()
    }


    /**
     * Initializing mediaController
     * */
    fun initializingPlayers(context: Context,title:String,mediaUri:String,thumbnail:String) {
        mainAudioUrl = "http://mind.harishparas.com/Videos/1.mp4"

        /** Initializing media controller */
        initializeMediaController(
            context = context,
            controller = { _ ->

            },
            addMediaData = {
                mediaController?.addMediaData(
                    mediaUri = mediaUri.toUri(),
                    title =  title,
                    artist = null,
                    playWhenReady = false,
                    image = thumbnail
                )
            }
        )
    }


    /**
     * this function is use to hit Contact Us Api
     * */
    suspend fun hitContactUsApi() {
        showLoading.postValue(true)

        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.NAME.value] = name.get()?.trim() ?: ""
        param[ApiConstants.ApiParams.EMAIL.value] = email.get()?.trim() ?: ""
        param[ApiConstants.ApiParams.SUBJECT.value] = subject.get()?.trim() ?: ""
        param[ApiConstants.ApiParams.MESSAGE.value] = message.get()?.trim() ?: ""

        authDataSourceImp.executeContactUsApi(param, apiType = ApiConstants.CONTACT_US).catch { e ->
            contactUsResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            contactUsResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Change password Api
     * */
    suspend fun hitChangePasswordApi() {
        showLoading.postValue(true)

        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.OLD_PASSWORD.value] = oldPassword.get()?.trim() ?: ""
        param[ApiConstants.ApiParams.PASSWORD.value] = password.get()?.trim() ?: ""

        authDataSourceImp.executeChangePasswordApi(param, apiType = ApiConstants.CHANGE_PASSWORD)
            .catch { e ->
                changePasswordResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                changePasswordResponse.emit(isResponse)
            }
    }

    /**
     * this function is use to hit Meditation Type List Api
     * */
    suspend fun hitMeditationTypeListApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.CATEGORY_ID.value] = categoryId.get()?.trim() ?: ""

        authDataSourceImp.executeMeditationTypeListApi(
            param,
            apiType = ApiConstants.MEDITATION_TYPE_LIST
        ).catch { e ->
            meditationTypeListResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            meditationTypeListResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Meditation Cat List Api
     * */
    suspend fun hitMeditationCatListApi() {
        showLoading.postValue(true)

        authDataSourceImp.executeMeditationCatListApi(apiType = ApiConstants.MEDITATION_CAT_LIST)
            .catch { e ->
                meditationCatListResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                meditationCatListResponse.emit(isResponse)
            }
    }


    /**
     * this function is use to hit Meditation Cat List Api
     * */
    suspend fun hitSearchApiAccordingToCatOrSubCatApi() {
        showLoading.postValue(true)
        val param = HashMap<String, Any?>()
        param[ApiConstants.ApiParams.TYPE_ID.value] = typeId.get() ?: 0
        param[ApiConstants.ApiParams.SEARCH_KEYWORD.value] = searchKeyword.get()?.trim() ?: ""

        authDataSourceImp.executeSearchCatOrSubCatApi(param,apiType = ApiConstants.SEARCH_API_DASHBOARD)
            .catch { e ->
                searchCatOrSubCatResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                searchCatOrSubCatResponse.emit(isResponse)
            }
    }

    /**
     * this function is use to hit Edification Type List Api
     * */
    suspend fun hitEdificationListApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.EDIFICATION_ID.value] = categoryId.get()?.trim() ?: ""
        authDataSourceImp.executeEdificationListApi(param, apiType = ApiConstants.EDIFICATION_LIST)
            .catch { e ->
                edificationListResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                edificationListResponse.emit(isResponse)
            }
    }


    /**
     * this function is use to hit Edification Category List Api
     * */
    suspend fun hitEdificationCategoryListApi() {
        showLoading.postValue(true)

        authDataSourceImp.executeEdificationCategoryListApi(apiType = ApiConstants.EDIFICATION_CATEGORY_LIST)
            .catch { e ->
                edificationCategoryListResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                edificationCategoryListResponse.emit(isResponse)
            }
    }

    /**
     * this function is use to hit Journal List Api
     * */
    suspend fun hitJournalListApi() {
        showLoading.postValue(true)

        authDataSourceImp.executeJournalListApi(apiType = ApiConstants.JOURNAL_LIST).catch { e ->
            journalListResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            journalListResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Add Journal Api
     * */
    suspend fun hitAddJournalApi() {
        showLoading.postValue(true)

        authDataSourceImp.executeAddJournalApi(jsonObjectAddJournalWeight.toString().createPartFromJsonString(), apiType = ApiConstants.ADD_JOURNAL)
            .catch { e ->
                addJournalResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                addJournalResponse.emit(isResponse)
            }
    }

    /**
     * this function is use to hit Resources List Api
     * */
    suspend fun hitResourceCategoryApi() {
        showLoading.postValue(true)

        authDataSourceImp.executeResourcesCategoryApi(apiType = ApiConstants.RESOURCE_CATEGORY).catch { e ->
            resourceListResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            resourceListResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Event List Api
     * */
    suspend fun hitEventListApi() {
        showLoading.postValue(true)

        authDataSourceImp.executeEventListApi(apiType = ApiConstants.EVENT_LIST).catch { e ->
            eventListResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            eventListResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Search Meditation List Api
     * */
    suspend fun hitSearchMeditationListApi() {
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.MEDITATION_SEARCH.value] =
            meditationSearchText.get()?.trim() ?: ""

        authDataSourceImp.executeSearchMediationApi(param, apiType = ApiConstants.SEARCH_MEDITATION)
            .catch { e ->
                searchMeditationResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                searchMeditationResponse.emit(isResponse)
            }
    }


    /**
     * this function is use to hit Update Notification Setting Api
     * */
    suspend fun hitUpdateNotificationSettingApi() {
        showLoading.postValue(true)

        val param = HashMap<String, Boolean?>()
        param[ApiConstants.ApiParams.IS_NOTIFICATION_ON.value] = isNotificationOn.get() ?: true
        authDataSourceImp.executeUpdateNotificationSettingApi(
            param,
            apiType = ApiConstants.UPDATE_NOTIFICATION_SETTING
        ).catch { e ->
            updateNotificationSettingResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            updateNotificationSettingResponse.emit(isResponse)
        }
    }


    /**
     * this function is use to hit Comment List Api
     * */
    suspend fun hitCommentListApi() {
        showLoading.postValue(true)
        val param = HashMap<String, Any?>()
        param[ApiConstants.ApiParams.EVENT_ID.value] = eventId.get()?.toInt()
        param[ApiConstants.ApiParams.TYPE.value] = type.get().toString().trim().toInt()

        Log.d("asdasdadasd",param.toString())

        authDataSourceImp.executeCommentListApi(param, apiType = ApiConstants.COMMENT_LIST)
            .catch { e ->
                commentListResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                commentListResponse.emit(isResponse)
            }
    }

    /**
     * this function is use to hit Like List Api
     * */
    suspend fun hitLikeListApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.EVENT_ID.value] = eventId.get() ?: ""
        authDataSourceImp.executeLikeListApi(param, apiType = ApiConstants.LIKE_LIST)
            .catch { e ->
                likeListResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                likeListResponse.emit(isResponse)
            }
    }

    /**
     * this function is use to hit Event Details Api
     * */
    suspend fun hitEventDetailsApi() {
        showLoading.postValue(true)

        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.EVENT_ID.value] = eventId.get()?.trim() ?: ""

        authDataSourceImp.executeEventDetailsApi(param, apiType = ApiConstants.EVENT_DETAILS)
            .catch { e ->
                eventDetailsResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                eventDetailsResponse.emit(isResponse)
            }
    }


    /**
     * this function is use to hit Update Journal Api
     * */
    suspend fun hitUpdateJournalApi() {
        showLoading.postValue(true)
        authDataSourceImp.executeUpdateJournalApi(jsonObjectAddJournalWeight.toString().createPartFromJsonString(), apiType = ApiConstants.UPDATE_JOURNAL)
            .catch { e ->
                updateJournalResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                updateJournalResponse.emit(isResponse)
            }
    }
    /**
     * this function is use to hit Notification List Api
     * */
    suspend fun hitNotificationListApi() {
        showLoading.postValue(true)
        authDataSourceImp.executeNotificationListApi(apiType = ApiConstants.NOTIFICATION_LIST)
            .catch { e ->
                notificationListResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                notificationListResponse.emit(isResponse)
            }
    }
    /**
     * this function is use to hit Notification List Api
     * */
    suspend fun hitNotificationReminderApi() {
        showLoading.postValue(true)
        val param = HashMap<String, Any?>()
        param[ApiConstants.ApiParams.REMINDER_TYPE_ID.value] = notificationTypeID.get() ?: 0
        param[ApiConstants.ApiParams.REMINDER_DATE.value] = notificationReminderDate.get()?.trim() ?: ""
        authDataSourceImp.executeNotificationReminderApi(param,apiType = ApiConstants.NOTIFICATION_ADD_REMINDER)
            .catch { e ->
                notificationReminderResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                notificationReminderResponse.emit(isResponse)
            }
    }
    /**
     * this function is use to hit Add Comment Api
     * */
    suspend fun hitAddCommentApi() {
//        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.COMMENT_DESC.value] = commentDesc.get()?.trim() ?: ""
        param[ApiConstants.ApiParams.EVENT_ID.value] = eventId.get()?.trim() ?: ""
        param[ApiConstants.ApiParams.TYPE.value] = type.get().toString().trim()
        authDataSourceImp.executeAddCommentApi(param, apiType = ApiConstants.ADD_COMMENT)
            .catch { e ->
                addCommentResponse.emit(Resource.Error(e.message.toString()))
                showLoading.postValue(false)
            }.collect { isResponse ->
                showLoading.postValue(false)
                addCommentResponse.emit(isResponse)
            }
    }

    /**
     * this function is use to hit Update Favourite Status Api
     * */
    suspend fun hitUpdateFavouriteEventStatusApi() {
//        showLoading.postValue(true)
        val param = HashMap<String, Any?>()
        param[ApiConstants.ApiParams.EVENT_ID.value] = eventId.get()?.trim() ?: ""
        param[ApiConstants.ApiParams.IS_FAVOURITE.value] = isFavourite.get() ?: false
        param[ApiConstants.ApiParams.TYPE.value] = type.get()?:1
        authDataSourceImp.executeUpdateFavouriteEventStatusApi(
            param,
            apiType = ApiConstants.UPDATE_FAVOURITE_EVENT_STATUS
        ).catch { e ->
            favouriteStatusEventResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            favouriteStatusEventResponse.emit(isResponse)
        }
    }
    /**
     * this function is use to hit User Profile Api
     * */
    suspend fun hitUserProfileApi() {
        showLoading.postValue(true)
        authDataSourceImp.executeUserProfileApi(apiType = ApiConstants.USER_PROFILE).catch { e ->
            userProfileResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            userProfileResponse.emit(isResponse)
        }
    }
    /**
     * this function is use to hit Logout Api
     * */
    suspend fun hitLogoutApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.USER_ID.value] = userId.get()?.trim() ?: ""
        authDataSourceImp.executeLogoutApi(param , apiType = ApiConstants.LOGOUT).catch { e ->
            logoutResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            logoutResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Get Journal Detail Api
     * */
    suspend fun hitGetJournalDetailApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.JOURNAL_ID.value] = journalID.get()?.trim() ?: ""
        authDataSourceImp.executeGetJournalDetailApi(param , apiType = ApiConstants.GET_JOURNAL_DETAIL).catch { e ->
            journalDetailResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            journalDetailResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Resource List By Type Api
     * */
    suspend fun hitResourceListByTypeApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.RESOURCE_TYPE_ID.value] = resourceTypeId.get()?.trim() ?: ""
        authDataSourceImp.executeResourceListByTypeApi(param , apiType = ApiConstants.RESOURCE_LIST_BY_TYPE).catch { e ->
            resourceListByTypeResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            resourceListByTypeResponse.emit(isResponse)
        }
    }


    /**
     * this function is use to hit Delete Journal Api
     * */
    suspend fun hitDeleteJournalApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.JOURNAL_ID.value] = journalID.get()?.trim() ?: ""
        authDataSourceImp.executeDeleteJournalApi(param , apiType = ApiConstants.DELETE_JOURNAL).catch { e ->
            deleteJournalResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            deleteJournalResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Notification Status Api
     * */
    suspend fun hitNotificationStatusApi() {
//        showLoading.postValue(true)
        authDataSourceImp.executeNotificationStatusApi( apiType = ApiConstants.NOTIFICATION_STATUS).catch { e ->
            notificationStatusResponse.emit(Resource.Error(e.message.toString()))
//            showLoading.postValue(false)
        }.collect { isResponse ->
//            showLoading.postValue(false)
            notificationStatusResponse.emit(isResponse)
        }
    }

    /**
     * this function is use to hit Chat Users Api
     * */
    suspend fun hitChatUsersApi() {
        showLoading.postValue(true)
        val param = HashMap<String, String?>()
        param[ApiConstants.ApiParams.USER_NAME.value] = userName.get()?.trim() ?: ""
        authDataSourceImp.executeChatUsersApi(param , apiType = ApiConstants.CHAT_USERS).catch { e ->
            chatUsersResponse.emit(Resource.Error(e.message.toString()))
            showLoading.postValue(false)
        }.collect { isResponse ->
            showLoading.postValue(false)
            chatUsersResponse.emit(isResponse)
        }
    }


    /** http://mind.harishparas.com/chatHub */
    /** webSocket connection **/
    fun connectSocket() {
        showLoading.postValue(true)
        signalRManager = SignalRManager()
        signalRManager?.apply {
            connectToHub(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN))
            if (isSignalRConnected()){
                sendCommandForUserList {
                    showLoading.postValue(false)
                    Log.d("signalR",Gson().toJson(it))
                    RunInScope.ioThread {
                        chatUsersListResponse.emit(it)
                    }
                }
            }else{
                showLoading.postValue(false)
            }
        }
    }

    fun getChatList(){
        showLoading.postValue(true)
        signalRManager = SignalRManager()
        signalRManager?.apply {
            connectToHub(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN))
            if (isSignalRConnected()) {
                sendCommandForChatList(sharedPrefs.getUserData()?.userId ?: 0, otherUSerId.get() ?: 0) {
                    showLoading.postValue(false)
                    RunInScope.ioThread {
                        messageListResponse.emit(it)
                    }
                }
            }else{
                showLoading.postValue(false)
                Log.d("SignalR", "Disconnected")
            }
        }
    }

    fun sendMessage(){
        signalRManager = SignalRManager()
        signalRManager?.apply {
            connectToHub(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN))
            if (isSignalRConnected()) {
                val request = HashMap<String,Any>()
                request["SenderId"] = sharedPrefs.getUserData()?.userId ?: 0
                request["ReceiverId"] = otherUSerId.get() ?: 0
                request["Message"] = messageText.get().toString()
                request["MessageType"] = "text"

                sendCommandForSendMessage(request) {
                    RunInScope.ioThread {
                        sendMessageResponse.emit(it)
                    }
                }
            }else{
                connectToHub(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN))
                Log.d("SignalR", "Disconnected")
            }
        }
    }

    fun getReceiverMessages(){
        signalRManager = SignalRManager()
        signalRManager?.apply {
            connectToHub(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN))
            if (isSignalRConnected()) {
                setOnMessageReceivedListener {
                    RunInScope.ioThread {
                        sendMessageResponse.emit(it)
                    }
                }
            }else{
                connectToHub(sharedPrefs.getString(AppConstants.USER_AUTH_TOKEN))
            }
        }

    }
    fun disconnectSocket(){
        signalRManager!!.disconnect()
    }
}









