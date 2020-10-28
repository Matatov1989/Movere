package com.matatov.movere.cluster

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.matatov.movere.R
import kotlin.math.roundToInt


class MyClusterManagerRenderer(var context: Context?, map: GoogleMap?, clusterManager: ClusterManager<ClusterMarker>?)
    : DefaultClusterRenderer<ClusterMarker>(context, map, clusterManager) {

    private var iconGenerator: IconGenerator? = null
    private var imageView: ImageView? = null
    private var markerWidth = 0
    private var markerHeight = 0

    init {
        iconGenerator = IconGenerator(context)
        imageView = ImageView(context)
        markerWidth = context!!.getResources().getDimension(R.dimen.custom_marker_image).roundToInt()
        markerHeight = context!!.getResources().getDimension(R.dimen.custom_marker_image).roundToInt()
        imageView!!.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)

        var padding = context!!.resources.getDimension(R.dimen.custom_marker_padding).toInt()
        imageView!!.setPadding(padding, padding, padding, padding)
        iconGenerator!!.setContentView(imageView)
    }

    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {

        imageView!!.setImageResource(item.iconPic)
        val icon = iconGenerator!!.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
    }


     override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>): Boolean {
        return false
    }

    /**
     * Update the GPS coordinate of a ClusterItem
     * @param clusterMarker
     */
    fun setUpdateMarker(clusterMarker: ClusterMarker) {
        val marker = getMarker(clusterMarker)
        if (marker != null) {
            marker.position = clusterMarker.position
        }
    }

    fun removeCluster(clusterMarker: ClusterMarker?) {
        val marker = getMarker(clusterMarker)
        marker?.remove()
    }
}
