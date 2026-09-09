package com.bsoft.compose.bmusic.ui.components

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TwoRowsTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bsoft.compose.bmusic.R

@Composable
fun TransparentTopBar(
    title: String, scrollBehavior: TopAppBarScrollBehavior? = null, @DrawableRes bg: Int,
    bitmap: Bitmap? = null, back: ()-> Unit = {}, playAll: ()-> Unit = {}, shuffle: ()-> Unit = {},
    add: (()-> Unit)? = null, menuClicked: (()-> Unit)? = null, details: List<String> = emptyList()) {
    TwoRowsTopAppBar(
        title = { expanded->
            if(expanded){
                Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(modifier = Modifier.size(120.dp), shape = RoundedCornerShape(12.dp)) {
                        if(bitmap == null){
                            Image(modifier = Modifier.fillMaxSize(), bitmap = ImageBitmap.imageResource(bg), contentScale = ContentScale.Crop, contentDescription = null)
                        }else{
                            BitmapImage(modifier = Modifier.fillMaxSize(), bitmap = bitmap, contentScale = ContentScale.Crop)
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(title, fontSize = 22.sp, lineHeight = 1.sp)
                        details.forEach {
                            Text(text = it, fontSize = 16.sp, lineHeight = 1.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            ListControls(add = add, playAll = playAll, shuffle = shuffle)
                            menuClicked?.let {
                                Icon(modifier = Modifier.clickable{ menuClicked() }, imageVector = ImageVector.vectorResource(R.drawable.circum__menu_kebab), contentDescription = null)
                            }
                        }
                    }
                }
            }else{
                Text(title)
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = Color.White, titleContentColor = Color.White,
            actionIconContentColor = Color.White),
        navigationIcon = {
            IconButton(onClick = { back() }) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.material_symbols__arrow_back_ios_new_rounded), contentDescription = "")
            }
        }
    )
}