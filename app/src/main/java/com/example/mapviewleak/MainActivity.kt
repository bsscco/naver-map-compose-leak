package com.example.mapviewleak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.NaverMap

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalNaverMapApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberAnimatedNavController()
                AnimatedNavHost(navController, startDestination = "home") {
                    composable(
                        route = "home",
                    ) { entry ->
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                count = 5,
                                key = { index -> index },
                            ) { index ->
                                when (index) {
                                    0 -> Box(
                                        modifier = Modifier.clickable {
                                            navController.navigate("detail")
                                        },
                                    ) {
                                        DummyItem(Color.Red)
                                    }
                                    1 -> DummyItem(Color.Blue)
                                    2 -> {
                                        // LazyColumn 안에서 스크롤로 인해 MapView가 여러 번 재생성 되면서 ON_PAUSE, ON_STOP, ON_DESTROY가 누락됩니다.
                                        NaverMap(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height((LocalConfiguration.current.screenHeightDp / 2).dp),
                                        )
                                    }
                                    3 -> DummyItem(Color.Green)
                                    4 -> DummyItem(Color.Yellow)
                                }
                            }
                        }
                    }

                    composable(
                        route = "detail",
                        // 슬라이드 애니메이션을 적용하면 NavBackStackEntry의 라이프사이클 중에서 ON_DESTROY가 누락됩니다.
                        enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Left) },
                        popExitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.Right) },
                    ) { entry ->
                        NaverMap(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }

    @Composable
    private fun DummyItem(color: Color) {
        Spacer(
            modifier = Modifier
                .background(color)
                .fillMaxWidth()
                .height((LocalConfiguration.current.screenHeightDp * 2 / 3).dp),
        )
    }
}