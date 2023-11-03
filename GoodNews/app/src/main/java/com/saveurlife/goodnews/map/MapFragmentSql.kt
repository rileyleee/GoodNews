package com.saveurlife.goodnews.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.saveurlife.goodnews.R
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.modules.ArchiveFileFactory
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay
import java.io.File


class MapFragmentSql : Fragment() {

    private lateinit var mapView: MapView
//    private lateinit var tileProvider: MapTileProviderBasic
//    private lateinit var basePath:File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_sql, container, false)
        mapView = view.findViewById(R.id.map)

//        tileProvider = MapTileProviderBasic(requireContext())
//        basePath = requireContext().filesDir

        initMap()

        return view
    }

    private fun initMap() {

        var sourceName: String? = null

//        val tilePath = basePath.absolutePath + File.separator + "tiles" + File.separator
//        val tilesOverlay = TilesOverlay(tileProvider, this.requireContext())
//
//
//        val customTileSource = XYTileSource(
//            "CustomTiles",
//            12,
//            12,
//            256,
//            ".png",
//            arrayOf("file://$tilePath")
//        )
//        mapView.setTileSource(customTileSource)
        mapView.setUseDataConnection(false)

//        tileProvider.tileSource = customTileSource
//
//
//        mapView.overlays.add(tilesOverlay)
//        mapView.tileProvider = tileProvider


        val f = File("${requireContext().filesDir.absolutePath}/osmdroid/")
        val files = f.listFiles()


        if (f.exists() && files != null && files.isNotEmpty()) {
            for (file in files) {
                val name = file.name.lowercase()

                if (name.endsWith(".sqlite")) {
                    try {
                        val tileProvider = OfflineTileProvider(
                            SimpleRegisterReceiver(requireActivity()),
                            arrayOf(file)
                        )
                        mapView.tileProvider = tileProvider

                        val archives = tileProvider.archives
                        if (archives.isNotEmpty()) {
                            val tileSources = archives[0].tileSources
                            if (tileSources.isNotEmpty()) {
                                sourceName = tileSources.iterator().next()
                                val tileSource = FileBasedTileSource.getSource(sourceName)
                                mapView.setTileSource(tileSource)
                            } else {
                                mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                            }
                        } else {
                            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                        }

                        Toast.makeText(
                            requireActivity(),
                            "Using ${file.absolutePath} $sourceName",
                            Toast.LENGTH_SHORT
                        ).show()
                        mapView.invalidate()
                        return
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "${file.absolutePath} sqlite 파일이 아님",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (f.exists() && (files == null || files.isEmpty())) {
            Toast.makeText(requireActivity(), "${f.absolutePath} 폴더 안에 파일이 없음", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(requireActivity(), "${f.absolutePath} 폴더를 찾지 못함", Toast.LENGTH_SHORT)
                .show()
        }

        mapView.zoomController.setZoomInEnabled(true)
        mapView.zoomController.setZoomOutEnabled(true)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(12.0)
        mapView.controller.setCenter(GeoPoint(37.566535, 126.9779692))
    }
}

