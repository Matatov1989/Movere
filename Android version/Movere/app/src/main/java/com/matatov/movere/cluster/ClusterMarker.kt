package com.matatov.movere.cluster

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.matatov.movere.models.UserModel


class ClusterMarker(position: LatLng?, title: String?, snippet: String?, var iconPic: Int, user: UserModel?) : ClusterItem {

    private var position: LatLng? = null
    private var title: String? = null
    private var snippet: String? = null
    var user: UserModel? = null

    init {
        this.position = position!!
        this.title = title!!
        this.snippet = snippet!!
        this.user = user!!
    }

    override fun getPosition(): LatLng {
        return this.position!!
    }

    override fun getTitle(): String? {
        return this.title!!
    }

    override fun getSnippet(): String? {
        return this.snippet!!
    }
}
