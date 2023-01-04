package com.xcaret.xcaret_hotel.photopass.ui

import com.xcaret.xcaret_hotel.R
import com.xcaret.xcaret_hotel.databinding.AlbumPpFragmentBinding
import com.xcaret.xcaret_hotel.view.base.BaseFragmentDataBinding
import com.xcaret.xcaret_hotel.photopass.vm.AlbumPPViewModel


class AlbumPPFragment : BaseFragmentDataBinding<AlbumPPViewModel, AlbumPpFragmentBinding>() {
    override fun getLayout(): Int = R.layout.album_pp_fragment

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val binding: AlbumPpFragmentBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayout(), container, false)
//
//        binding.lifecycleOwner = this
//        binding.executePendingBindings()
//        return binding.root
//    }


    override fun getViewModelClass(): Class<AlbumPPViewModel> = AlbumPPViewModel::class.java

    override fun bindViewToModel() { binding.albumPpViewModel = viewModel }

    override fun setupUI() {

    }
}