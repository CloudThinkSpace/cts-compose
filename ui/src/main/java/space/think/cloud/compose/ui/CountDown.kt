package space.think.cloud.compose.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CountDown(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.DarkGray,
    circleColor: Color = Color.DarkGray,
    initialValue: Int = 5,
    fontSize: Int = 15,
    fontColor: Color = Color.White,
    onComplete: () -> Unit,
) {

    // 定义圆中心位置，默认为0
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    // 扇形当前分割分数
    var remainingSeconds by remember {
        mutableIntStateOf(initialValue)
    }

    // 扇形当前分割分数
    var sweepNum by remember {
        mutableIntStateOf(initialValue * 20)
    }

    // 扇形
    var sweepAngle by remember {
        mutableFloatStateOf(-360f)
    }

    // 是否跳过读秒
    var skipCount by remember {
        mutableStateOf(false)
    }

    // 定义协程
    LaunchedEffect(Unit) {

        while (sweepNum > 0 && !skipCount) {
            delay(50)
            sweepNum -= 1
            sweepAngle = -360f + (initialValue * 20 - sweepNum) * 360f / (initialValue * 20)
            if (sweepNum % 20 == 0) {
                remainingSeconds -= 1
            }
        }
        onComplete()

    }

    Box(modifier = modifier.clickable {
        skipCount = true
    }) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // 画布宽
            val width = size.width
            // 画布高
            val height = size.height
            // 圆环框
            val circleThickness = width / 20f
            //直径
            val diameter = if (width < height) width else height

            // 中心点
            circleCenter = Offset(
                x = width / 2f,
                y = height / 2f
            )

            // 画圆
            drawArc(
                color = circleColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(
                    width = diameter,
                    height = diameter
                ),
                topLeft = Offset(
                    x = (width - diameter) / 2,
                    y = (height - diameter) / 2
                )
            )

            // 画圆环
            drawCircle(
                style = Stroke(
                    width = circleThickness
                ),
                color = secondaryColor,
                radius = diameter / 2 - circleThickness / 2,
                center = circleCenter
            )

            // 画圆弧
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                style = Stroke(
                    width = circleThickness,
                    cap = StrokeCap.Round
                ),
                useCenter = false,
                size = Size(
                    width = diameter - circleThickness,
                    height = diameter - circleThickness
                ),
                topLeft = Offset(
                    x = (circleThickness + width - diameter) / 2,
                    y = (circleThickness + height - diameter) / 2
                )
            )

            // 画文字
            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$remainingSeconds",
                        circleCenter.x,
                        circleCenter.y + fontSize.dp.toPx() / 3f,
                        Paint().apply {
                            textSize = fontSize.sp.toPx()
                            textAlign = Paint.Align.CENTER
                            color = fontColor.toArgb()
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CountDown(
            modifier = Modifier.size(100.dp),
            initialValue = 50,
            primaryColor = Color.Blue,
            secondaryColor = Color.Yellow,
            circleColor = Color.Cyan,
            fontColor = Color.DarkGray,
            fontSize = 25

        ) {
        }
    }
}