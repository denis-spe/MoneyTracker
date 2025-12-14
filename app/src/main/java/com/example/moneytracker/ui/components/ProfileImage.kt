package com.example.moneytracker.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.moneytracker.R

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    accountSpecificUrl: Uri?,
    currentAccountId: String,
    size: Int = 100,
    color: Color = Color.LightGray
) {
    val context = LocalContext.current
    val uriString = accountSpecificUrl?.toString() ?: "null"
    val uniqueCacheKey = "${currentAccountId}_$uriString"

    Log.d(
        "ProfileImageDebug",
        "accountId=$currentAccountId uri=$uriString cacheKey=$uniqueCacheKey"
    )

    val request = ImageRequest.Builder(context)
        .data(uriString) // pass string for clarity
        .diskCacheKey(uniqueCacheKey)
        // disable cache (debug only)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .diskCachePolicy(CachePolicy.DISABLED)
        .crossfade(true)
        .build()

    if (accountSpecificUrl != null) {
        AsyncImage(
            model = request,
            contentDescription = "User Profile Image",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size.dp)
                .clip(CircleShape),
        )
    } else {
        Column(
            modifier = modifier
                .size(size.dp)
                .border(1.dp, color, CircleShape)
                .clip(CircleShape),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.default_user),
                contentDescription = "Default User Image",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(color)
            )
        }
    }
}