package com.waqarahmad.arrowescape

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

private data class Arrow(val row:Int, val col:Int, val dir:Int, val color:Color)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ArrowEscapeApp() }
    }
}

@Composable
fun ArrowEscapeApp() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7C5CFF),
            secondary = Color(0xFF20D9C4),
            background = Color(0xFF080B16),
            surface = Color(0xFF11172A)
        )
    ) {
        var level by remember { mutableIntStateOf(1) }
        var score by remember { mutableIntStateOf(0) }
        var mistakes by remember { mutableIntStateOf(0) }
        var hint by remember { mutableStateOf(false) }
        var solved by remember { mutableStateOf(false) }

        val arrows = remember(level) { makeLevel(level) }
        val removed = remember(level) { mutableStateListOf<Int>() }
        val pulse by animateFloatAsState(if (hint) 1.12f else 1f, label="hint")

        fun tap(index:Int) {
            if (solved || removed.contains(index)) return
            val arrow = arrows[index]
            if (canExit(arrow, arrows, removed)) {
                removed.add(index)
                score += 10 + level
                if (removed.size == arrows.size) solved = true
            } else {
                mistakes++
                score = max(0, score - 2)
            }
        }

        if (solved) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Level $level Complete! 🎉") },
                text = { Text("Score: $score\nMistakes: $mistakes") },
                confirmButton = {
                    Button(onClick = {
                        level++
                        mistakes = 0
                        solved = false
                        hint = false
                    }) { Text("NEXT LEVEL") }
                }
            )
        }

        Column(
            Modifier.fillMaxSize().background(Color(0xFF080B16)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("ARROW ESCAPE", color=Color.White, fontSize=24.sp, fontWeight=FontWeight.ExtraBold)
                    Text("by Waqar Ahmad", color=Color(0xFF8E98B8), fontSize=12.sp)
                }
                Text("LEVEL $level", color=Color(0xFF20D9C4), fontWeight=FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceEvenly) {
                Stat("SCORE", score.toString())
                Stat("MISTAKES", mistakes.toString())
                Stat("LEFT", (arrows.size-removed.size).toString())
            }

            Spacer(Modifier.height(18.dp))

            Box(
                Modifier.fillMaxWidth().aspectRatio(1f).background(Color(0xFF10162A), RoundedCornerShape(24.dp)),
                contentAlignment=Alignment.Center
            ) {
                ArrowBoard(arrows, removed, hint, pulse, ::tap)
            }

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick={ hint=true }) { Text("💡 HINT") }
                Button(onClick={
                    removed.clear(); mistakes=0; score=max(0, score-5); hint=false
                }) { Text("↻ RESTART") }
            }

            Spacer(Modifier.height(10.dp))
            AnimatedVisibility(hint) {
                Text(
                    "Hint: remove the glowing arrow next.",
                    color=Color(0xFFFFD166),
                    fontSize=13.sp
                )
            }
            Spacer(Modifier.weight(1f))
            Text("Clear every arrow by moving it toward its exit.", color=Color(0xFF69728F), fontSize=12.sp)
        }
    }
}

@Composable
private fun Stat(label:String, value:String) {
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
        Text(value, color=Color.White, fontWeight=FontWeight.Bold, fontSize=18.sp)
        Text(label, color=Color(0xFF69728F), fontSize=10.sp)
    }
}

@Composable
private fun ArrowBoard(
    arrows:List<Arrow>,
    removed:List<Int>,
    hint:Boolean,
    pulse:Float,
    onTap:(Int)->Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(18.dp)) {
        val n = sqrt(arrows.size.toDouble()).toInt().coerceAtLeast(3)
        val cell = minOf(maxWidth.value, maxHeight.value) / n
        Canvas(Modifier.fillMaxSize()) {
            // grid
            for (i in 0..n) {
                drawLine(Color(0xFF1A2340), Offset(i*cell,0f), Offset(i*cell,n*cell), strokeWidth=1f)
                drawLine(Color(0xFF1A2340), Offset(0f,i*cell), Offset(n*cell,i*cell), strokeWidth=1f)
            }
        }
        arrows.forEachIndexed { index, a ->
            if (!removed.contains(index)) {
                val x = a.col*cell
                val y = a.row*cell
                val isHint = hint && canExit(a, arrows, removed)
                Box(
                    Modifier.offset(x.dp, y.dp)
                        .size(cell.dp)
                        .clickable { onTap(index) }
                ) {
                    Canvas(Modifier.fillMaxSize().padding(8.dp)) {
                        val c = if (isHint) Color(0xFFFFD166) else a.color
                        val cx=size.width/2; val cy=size.height/2
                        val len=min(size.width,size.height)*.33f*pulse
                        val head=len*.42f
                        val dx=when(a.dir){1->1f;3->-1f;else->0f}
                        val dy=when(a.dir){2->1f;0->-1f;else->0f}
                        val start=Offset(cx-dx*len*.65f, cy-dy*len*.65f)
                        val end=Offset(cx+dx*len*.65f, cy+dy*len*.65f)
                        drawLine(c,start,end,strokeWidth=9f)
                        val left=Offset(end.x-dx*head-dy*head*.7f,end.y-dy*head+dx*head*.7f)
                        val right=Offset(end.x-dx*head+dy*head*.7f,end.y-dy*head-dx*head*.7f)
                        val p=Path().apply { moveTo(end.x,end.y); lineTo(left.x,left.y); lineTo(right.x,right.y); close() }
                        drawPath(p,c)
                    }
                }
            }
        }
    }
}

private fun makeLevel(level:Int):List<Arrow> {
    val n = when {
        level < 4 -> 3
        level < 10 -> 4
        level < 20 -> 5
        else -> 6
    }
    val count=n*n
    val colors=listOf(Color(0xFF7C5CFF),Color(0xFF20D9C4),Color(0xFFFF5C8A),Color(0xFFFFB84D))
    return List(count) { i ->
        val r=i/n; val c=i%n
        val dir = when((i*37 + level*13) % 4) { 0->0;1->1;2->2;else->3 }
        Arrow(r,c,dir,colors[(i+level)%colors.size])
    }
}

private fun canExit(a:Arrow, arrows:List<Arrow>, removed:List<Int>):Boolean {
    val n=sqrt(arrows.size.toDouble()).toInt()
    var r=a.row; var c=a.col
    val dr=when(a.dir){2->1;0->-1;else->0}
    val dc=when(a.dir){1->1;3->-1;else->0}
    while(true){
        r+=dr; c+=dc
        if(r !in 0 until n || c !in 0 until n) return true
        val blocker=arrows.indexOfFirst { it.row==r && it.col==c && !removed.contains(arrows.indexOf(it)) }
        if(blocker>=0) return false
    }
}
