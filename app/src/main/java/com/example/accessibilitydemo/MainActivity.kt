package com.example.accessibilitydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.AccessibilityAction
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.accessibilitydemo.ui.theme.AccessibilityDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            AccessibilityDemoTheme {
                Exercise1_3Screen()
            }
        }
    }
}

@Composable
fun ProfileIconWithContentDescription() {
    // TASK: Implement contentDescription using stringResource()
    val description = stringResource(R.string.profile_icon_description)

    Icon(
        imageVector = Icons.Filled.Person,
        contentDescription = description, // <- Accessibility implementation here
        modifier = Modifier.size(48.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun Exercise1_1Screen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Accessibility Exercise 1.1",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 1. Standard Content Description Example
            Card(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileIconWithContentDescription()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "User Profile Name")
                }
                Text(
                    text = "Screen Reader Announcement: \"User profile icon, User Profile Name\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                )
            }

            // 2. Custom Semantics Example
            Card(modifier = Modifier.padding(vertical = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Product Review")
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomStarRating(rating = 4)
                }
                Text(
                    text = "Screen Reader Announcement: \"Rating: 4 out of 5 stars\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CustomStarRating(rating: Int) {
    val totalStars = 5
    // TASK: Use Modifier.semantics to provide descriptive text for the custom composable
    val ratingDescription = stringResource(R.string.star_rating_label).format(rating)

    Row(
        modifier = Modifier
            .semantics {
                // <- Accessibility implementation here: Set the descriptive text
                contentDescription = ratingDescription
            }
            // Add a visual indicator to show the composable is grouped
            .padding(4.dp)
    ) {
        repeat(totalStars) { index ->
            Icon(
                // Use a standard Icon/Image to represent the custom drawn component
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                // IMPORTANT: contentDescription MUST be null here, so the screen reader
                // only announces the single, complete description on the parent Row/semantics block.
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFA000) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PostActionsRow(
    onLikeClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Like Button (made invisible to the screen reader)
        TextButton(
            onClick = onLikeClicked,
            modifier = Modifier.semantics { invisibleToUser() } // Hides this action from TalkBack
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Like")
        }

        // Share Button (made invisible to the screen reader)
        TextButton(
            onClick = onShareClicked,
            modifier = Modifier.semantics { invisibleToUser() } // Hides this action from TalkBack
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Share")
        }
    }
}

@Composable
fun AccessiblePostCard(
    postAuthor: String = "Jane Doe",
    postContent: String = "Just finished the accessibility module! Great practical tips on semantics.",
    onActionFeedback: (String) -> Unit
) {
    // 1. Define the actions
    val likeAction = CustomAccessibilityAction(
        label = "Like Post"
    ) {
        onActionFeedback("Liked post by $postAuthor!")
        true // Return true if the action was successfully performed
    }

    val shareAction = CustomAccessibilityAction(
        label = "Share Post"
    ) {
        onActionFeedback("Shared post by $postAuthor!")
        true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = { /* Default action or navigation */ })
            // 2. Clear all semantics on this element and its children, then define new ones.
            // This ensures the screen reader only sees the Card and the custom actions.
            .clearAndSetSemantics {
                // Set the basic content description (what the card is)
                contentDescription = "$postAuthor: $postContent"

                // 3. Define the custom actions for TalkBack
                customActions = listOf(likeAction, shareAction)

                // Optional: Define an on-click action for the Card itself (navigation)
                onClick("View Post Details") {
                    onActionFeedback("Navigated to Post Details.")
                    true
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = postAuthor,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(text = postContent, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))

            // Visual elements (Like/Share buttons) that are made invisible to TalkBack
            PostActionsRow(
                onLikeClicked = { onActionFeedback("Like button (Visual) clicked") },
                onShareClicked = { onActionFeedback("Share button (Visual) clicked") }
            )
        }
    }
}

@Composable
fun Exercise1_2Screen() {
    var feedbackMessage by remember { mutableStateOf("Ready to test accessibility actions.") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Accessibility Exercise 1.2: Custom Actions",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Display current feedback from the triggered accessibility action
            Text(
                text = "Feedback: $feedbackMessage",
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AccessiblePostCard(onActionFeedback = { feedbackMessage = it })

            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "1. Enable TalkBack on your device or use the Accessibility Checker.\n" +
                        "2. Tap the card. TalkBack announces the card content.\n" +
                        "3. Swipe up or down to reveal 'Custom Actions' (Like Post, Share Post, View Post Details).\n" +
                        "4. Selecting a custom action will update the feedback above.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun LiveRegionCounter() {
    // State to hold the current count
    var count by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Total Items Added:",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.padding(8.dp))

        // 1. Live Region Text: This Text Composable is marked as a live region.
        Text(
            text = "$count items",
            style = MaterialTheme.typography.displayLarge,
            // TASK: Apply the Modifier.semantics block with liveRegion = LiveRegionMode.Polite
            modifier = Modifier
                .semantics {
                    // This is the key: tells the screen reader to announce
                    // this text when its content changes.
                    liveRegion = LiveRegionMode.Assertive
                }
        )

        Spacer(modifier = Modifier.padding(24.dp))

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { count = (count - 1).coerceAtLeast(0) }) {
                Text("Remove")
            }
            Button(onClick = { count++ }) {
                Text("Add Item")
            }
        }

        Spacer(modifier = Modifier.padding(24.dp))

        Text(
            text = "When an item is added or removed, TalkBack will announce the new count automatically without losing focus, thanks to LiveRegionMode.Polite.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun Exercise1_3Screen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {Text(
            text = "Accessibility Exercise 1.3: Live Regions",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )


            LiveRegionCounter()
        }
    }
}
