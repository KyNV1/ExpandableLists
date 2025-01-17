package com.jetpack.expandablelists

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpack.expandablelists.model.SampleData
import com.jetpack.expandablelists.ui.theme.Purple500
import com.jetpack.expandablelists.utils.Constants.CollapseAnimation
import com.jetpack.expandablelists.utils.Constants.ExpandAnimation
import com.jetpack.expandablelists.utils.Constants.FadeInAnimation
import com.jetpack.expandablelists.utils.Constants.FadeOutAnimation
import com.jetpack.expandablelists.viewmodel.ExpandableViewModel

@ExperimentalAnimationApi
@Composable
fun ExpandableScreen(viewModel: ExpandableViewModel) {
    val cards = viewModel.cards.collectAsState()
    val expandedCard = viewModel.expandedCardList.collectAsState()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Purple500),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Expandable Lists",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            LazyColumn {
                itemsIndexed(cards.value) { _, card ->
                    ExpandableCard(
                        card = card,
                        onCardArrowClick = { viewModel.cardArrowClick(card.id) },
                        expanded = expandedCard.value.contains(card.id)
                    )
                }
            }
        }
    }
}


@ExperimentalAnimationApi
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    card: SampleData,
    onCardArrowClick: () -> Unit,
    expanded: Boolean,
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(targetState = transitionState, label = "transition")
    val cardBgColor by transition.animateColor({
        tween(durationMillis = ExpandAnimation)
    }, label = "bgColorTransition") {
        if (expanded) Purple500 else Purple500
    }
    val cardPaddingHorizontal by transition.animateDp({
        tween(durationMillis = ExpandAnimation)
    }, label = "paddingTransition") {
        20.dp
    }
    val cardElevation by transition.animateDp({
        tween(durationMillis = ExpandAnimation)
    }, label = "elevationTransition") {
        if (expanded) 20.dp else 5.dp
    }
    val cardRoundedCorners by transition.animateDp({
        tween(
            durationMillis = ExpandAnimation,
            easing = FastOutSlowInEasing
        )
    }, label = "cornersTransition") {
        15.dp
    }
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = ExpandAnimation)
    }, label = "rotationDegreeTransition") {
        if (expanded) 0f else 180f
    }

    Card(
        backgroundColor = cardBgColor,
        elevation = cardElevation,
        shape = RoundedCornerShape(cardRoundedCorners),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = cardPaddingHorizontal,
                vertical = 8.dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(0.85f)
                    ) {
                        Text(
                            text = card.title,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(0.15f)
                    ) {
                        CardArrow(
                            degrees = arrowRotationDegree,
                            onClick = onCardArrowClick
                        )
                    }
                }
            }
            ExpandableContent(expanded)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun ExpandableCardPreview() {
    ExpandableCard(card = SampleData(1,"KyNV1"), onCardArrowClick = { /*TODO*/ }, expanded = true)
}
@Composable
fun CardArrow(
    degrees: Float,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_expand_less_24),
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
                tint = Color.White
            )
        }
    )
}

@Preview()
@Composable
fun CardArrowPreview() {
    CardArrow(degrees = 0f) {

    }
}

@ExperimentalAnimationApi
@Composable
fun ExpandableContent(expanded: Boolean = true) {
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = FadeInAnimation,
                easing = FastOutLinearInEasing
            )
        )
    }
    val enterExpand = remember {
        expandVertically(animationSpec = tween(ExpandAnimation))
    }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = FadeOutAnimation,
                easing = LinearOutSlowInEasing
            )
        )
    }
    val exitCollapse = remember {
        shrinkVertically(animationSpec = tween(CollapseAnimation))
    }

    AnimatedVisibility(
        visible = expanded,
        enter = enterExpand + enterFadeIn,
        exit = exitCollapse + exitFadeOut
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        ) {
            Text(
                text = "Make It Easy Description",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                color = Purple500
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun ExpandableContentPreview() {
    ExpandableContent(true)
}