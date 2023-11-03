package com.saveurlife.goodnews.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saveurlife.goodnews.R
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay
import java.io.File


class MapFragment1 : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var tileProvider: MapTileProviderBasic
    private lateinit var basePath:File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map1, container, false)
        mapView = view.findViewById(R.id.mapView)

        tileProvider = MapTileProviderBasic(requireContext())
        basePath = requireContext().filesDir

        initMap()

        return view
    }

    private fun initMap() {

        val tilePath = basePath.absolutePath + File.separator + "tiles" + File.separator
        val tilesOverlay = TilesOverlay(tileProvider, this.requireContext())

        // 특정 타일 파일의 존재 여부를 확인
        val tileFile = File(tilePath, "12/3548/1601.png")
        if (tileFile.exists()) {
            Log.d("MapFragment", "있다고")
        } else {
            Log.d("MapFragment", "없어")
        }

        val customTileSource = XYTileSource(
            "CustomTiles",
            12,
            12,
            256,
            ".png",
            arrayOf("file://$tilePath")
        )
        mapView.setTileSource(customTileSource)
        mapView.setUseDataConnection(false)

        tileProvider.tileSource = customTileSource


        mapView.overlays.add(tilesOverlay)
        mapView.tileProvider = tileProvider

        mapView.zoomController.setZoomInEnabled(true)
        mapView.zoomController.setZoomOutEnabled(true)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(GeoPoint(37.566535, 126.9779692))
    }

}