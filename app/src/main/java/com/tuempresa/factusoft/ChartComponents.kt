package com.tuempresa.factusoft

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * Colores para gráficos
 */
val ChartColors = listOf(
    Color(0xFF2196F3),  // Azul
    Color(0xFF4CAF50),  // Verde
    Color(0xFFFF9800),  // Naranja
    Color(0xFF9C27B0),  // Morado
    Color(0xFFF44336),  // Rojo
    Color(0xFF00BCD4),  // Cyan
    Color(0xFFFFC107),  // Amarillo
    Color(0xFF795548)   // Marrón
)

/**
 * Gráfico de pastel
 */
@Composable
fun PieChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    showLegend: Boolean = true
) {
    val total = data.sumOf { it.second }
    if (total == 0.0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay datos disponibles", fontSize = 14.sp, color = Color(0xFF666666))
        }
        return
    }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
        ) {
            val canvasSize = minOf(size.width, size.height)
            val radius = canvasSize / 2f - 40f
            val center = Offset(size.width / 2f, size.height / 2f)
            
            var startAngle = -90f // Comenzar desde arriba
            
            // Primero dibujar todos los arcos
            data.forEachIndexed { index, (_, value) ->
                val sweepAngle = (value / total * 360f).toFloat()
                val color = ChartColors[index % ChartColors.size]
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                
                startAngle += sweepAngle
            }
            
            // Luego dibujar las líneas separadoras
            startAngle = -90f
            data.forEachIndexed { index, (_, value) ->
                val sweepAngle = (value / total * 360f).toFloat()
                val lineAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val lineStart = Offset(
                    center.x + (radius * 0.7f * cos(lineAngle).toFloat()),
                    center.y + (radius * 0.7f * sin(lineAngle).toFloat())
                )
                val lineEnd = Offset(
                    center.x + (radius * 1.1f * cos(lineAngle).toFloat()),
                    center.y + (radius * 1.1f * sin(lineAngle).toFloat())
                )
                
                drawLine(
                    color = Color(0xFF333333),
                    start = lineStart,
                    end = lineEnd,
                    strokeWidth = 2f
                )
                
                startAngle += sweepAngle
            }
        }
        
        if (showLegend) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                data.forEachIndexed { index, (label, value) ->
                    val color = ChartColors[index % ChartColors.size]
                    val percentage = (value / total * 100).toFloat()
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = String.format("$%,.2f (%.1f%%)", value, percentage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Gráfico de barras verticales
 */
@Composable
fun BarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    maxValue: Double? = null,
    showValues: Boolean = true,
    barColor: Color = Color(0xFF2196F3)
) {
    val maxVal = maxValue ?: (data.maxOfOrNull { it.second } ?: 1.0)
    if (maxVal == 0.0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay datos disponibles", fontSize = 14.sp, color = Color(0xFF666666))
        }
        return
    }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val barWidth = (size.width - 32f) / data.size - 16f
                val maxHeight = size.height - 60f
                val spacing = 16f
                
                data.forEachIndexed { index, (_, value) ->
                    val barHeight = (value / maxVal * maxHeight).toFloat().coerceAtLeast(10f)
                    val x = 16f + index * (barWidth + spacing) + spacing / 2f
                    val y = size.height - 50f - barHeight
                    
                    // Dibujar barra
                    drawRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight)
                    )
                }
            }
            
            // Etiquetas y valores usando composables
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEachIndexed { index, (label, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (showValues) {
                            Text(
                                text = String.format("$%.0f", value),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = if (label.length > 8) label.substring(0, 6) + ".." else label,
                            fontSize = 9.sp,
                            color = Color(0xFF666666),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Gráfico de barras horizontales
 */
@Composable
fun HorizontalBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    maxValue: Double? = null,
    showValues: Boolean = true
) {
    val maxVal = maxValue ?: (data.maxOfOrNull { it.second } ?: 1.0)
    if (maxVal == 0.0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay datos disponibles", fontSize = 14.sp, color = Color(0xFF666666))
        }
        return
    }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        data.forEachIndexed { index, (label, value) ->
            val color = ChartColors[index % ChartColors.size]
            val percentage = (value / maxVal * 100).toFloat()
            val barHeight = 30.dp
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (label.length > 25) label.substring(0, 23) + ".." else label,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .weight(0.4f)
                            .padding(end = 8.dp),
                        maxLines = 1
                    )
                    
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(barHeight)
                            .background(Color(0xFFE0E0E0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(percentage / 100f)
                                .background(color)
                        )
                        
                        if (showValues && percentage > 15f) {
                            Text(
                                text = String.format("$%,.2f", value),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = String.format("$%,.2f", value),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.weight(0.3f)
                    )
                }
            }
        }
    }
}

