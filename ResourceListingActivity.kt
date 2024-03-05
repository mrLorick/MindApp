package com.mindbyromanzanoni.view.activity.resourceListing

import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.data.response.resource.ResourceTypeList
import com.mindbyromanzanoni.databinding.ActivityResourceListingBinding
import com.mindbyromanzanoni.databinding.RowitemResourceslistBinding
import com.mindbyromanzanoni.genrics.GenericAdapter
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.gone
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.utils.visible
import com.mindbyromanzanoni.view.activity.openPdfViewer.OpenPdfActivity
import com.mindbyromanzanoni.view.activity.resource.ResourceDetailActivity
import com.mindbyromanzanoni.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResourceListingActivity : BaseActivity<ActivityResourceListingBinding>() {
    private val viewModal: HomeViewModel by viewModels()
    private val activity = this@ResourceListingActivity
    private var catName = ""
    override fun getLayoutRes() = R.layout.activity_resource_listing
    override fun initView() {
        getIntentData()
        seToolBar()
        observeDataFromViewModal()
        hitResourceTypeListApi()
    }

    private fun hitResourceTypeListApi() {
        RunInScope.ioThread {
            viewModal.hitResourceListByTypeApi()
        }
    }
    override fun viewModel() {}
    private fun seToolBar() {
        binding.toolbar.apply {
            tvToolTitle.text = catName
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }

    /** get Intent data from
     * MeditationsFragment
     * */
    private fun getIntentData() {
        val intent = intent.extras
        if (intent != null) {
            viewModal.resourceTypeId.set(intent.getString(AppConstants.RESOURCE_TYPE_ID).toString())
            catName = intent.getString(AppConstants.CAT_NAME).toString()
        }
    }


    /** set recycler view Meditation  List */
    private fun initMeditationRecyclerView(data: ArrayList<ResourceTypeList>) {
        binding.rvResourcesList.adapter = resourcesCategoryListAdapter
        resourcesCategoryListAdapter.submitList(data)
    }

    private val resourcesCategoryListAdapter =
        object : GenericAdapter<RowitemResourceslistBinding, ResourceTypeList>() {
            override fun getResourceLayoutId(): Int {
                return R.layout.rowitem_resourceslist
            }

            override fun onBindHolder(
                holder: RowitemResourceslistBinding,
                dataClass: ResourceTypeList,
                position: Int
            ) {

                holder.apply {
                    ivImage.setImageFromUrl(R.drawable.placeholder_mind, dataClass.imageName)
                    tvTitle.text = dataClass.title
                    tvDesc.text = dataClass.content.trimStart()
                    /*tvDuration.text = dataClass.duration
                    if (dataClass.duration == null) {
                        tvDuration.gone()
                    } else {
                        tvDuration.visible()
                    }*/
                    tvDuration.gone()
                    root.setOnClickListener {
                        val dataJson = Gson().toJson(dataClass)
                        val bundle = bundleOf(AppConstants.RESOURCE_DETAILS to dataJson)
                        launchActivity<ResourceDetailActivity>(0, bundle) { }

                    }
                    tvViewPdf.setOnClickListener {
                        if (dataClass.pdfFileName != null) {
                            val bundle = bundleOf(AppConstants.PDF_URL to dataClass.pdfFileName)
                            launchActivity<OpenPdfActivity>(0, bundle) { }
                        } else {
                            showErrorSnack(this@ResourceListingActivity, "No pdf file is added")
                        }
                    }
                }
            }
        }


    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.resourceListByTypeSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            if (data.data.isEmpty()) {
                                binding.noDataFound.visible()
                            } else {
                                binding.noDataFound.gone()
                                initMeditationRecyclerView(data.data)
                            }
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }

                    is Resource.Error -> {
                        isResponse.message?.let { msg -> showErrorSnack(activity, msg) }
                    }
                }
            }
        }

        viewModal.showLoading.observe(activity) {
            if (it) {
                binding.rvResourcesList.gone()
                binding.shimmerCommentList.apply {
                    visible()
                    startShimmer()
                }
            } else {
                binding.shimmerCommentList.apply {
                    RunInScope.mainThread {
                        stopShimmer()
                        gone()
                        binding.rvResourcesList.visible()
                    }
                }
            }
        }
    }
}