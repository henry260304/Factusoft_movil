package com.tuempresa.factusoft

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generador de PDFs para reportes
 */
object PdfGenerator {
    
    private const val PAGE_WIDTH = 595 // A4 width in points (210mm)
    private const val PAGE_HEIGHT = 842 // A4 height in points (297mm)
    private const val MARGIN = 40f
    private const val LINE_HEIGHT = 18f
    private const val HEADER_HEIGHT = 60f
    private const val FOOTER_HEIGHT = 40f
    private const val CONTENT_HEIGHT = PAGE_HEIGHT - MARGIN * 2 - HEADER_HEIGHT - FOOTER_HEIGHT
    private const val ROWS_PER_PAGE = 30
    
    /**
     * Genera un PDF con la lista de productos
     */
    fun generateProductReportPdf(context: Context, products: List<ProductReport>): File? {
        try {
            val pdfDocument = PdfDocument()
            
            // Crear el nombre del archivo con fecha y hora
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "Reporte_Productos_${dateFormat.format(Date())}.pdf"
            
            // Crear el directorio de archivos de la app si no existe
            val appDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Reports")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            
            val file = File(appDir, fileName)
            
            // Dividir productos en páginas
            val totalPages = (products.size / ROWS_PER_PAGE) + if (products.size % ROWS_PER_PAGE > 0) 1 else 0
            
            for (pageNum in 0 until totalPages) {
                val startIndex = pageNum * ROWS_PER_PAGE
                val endIndex = minOf(startIndex + ROWS_PER_PAGE, products.size)
                val pageProducts = products.subList(startIndex, endIndex)
                
                // Crear una página
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                var yPosition = MARGIN
                
                // Pintura para el título
                val titlePaint = Paint().apply {
                    textSize = 24f
                    isFakeBoldText = true
                    textAlign = Paint.Align.CENTER
                }
                
                // Pintura para encabezados
                val headerPaint = Paint().apply {
                    textSize = 12f
                    isFakeBoldText = true
                    color = android.graphics.Color.WHITE
                }
                
                // Pintura para texto normal
                val textPaint = Paint().apply {
                    textSize = 10f
                    color = android.graphics.Color.BLACK
                }
                
                // Pintura para números
                val numberPaint = Paint().apply {
                    textSize = 10f
                    textAlign = Paint.Align.RIGHT
                    color = android.graphics.Color.BLACK
                }
                
                // Pintura para stock bajo
                val lowStockPaint = Paint().apply {
                    textSize = 10f
                    textAlign = Paint.Align.RIGHT
                    color = android.graphics.Color.parseColor("#F44336")
                }
                
                // Pintura para stock normal
                val normalStockPaint = Paint().apply {
                    textSize = 10f
                    textAlign = Paint.Align.RIGHT
                    color = android.graphics.Color.parseColor("#4CAF50")
                }
                
                // Título
                canvas.drawText("LISTA DE PRODUCTOS", PAGE_WIDTH / 2f, yPosition + 30f, titlePaint)
                yPosition += HEADER_HEIGHT
                
                // Información del reporte
                val infoPaint = Paint().apply {
                    textSize = 9f
                    color = android.graphics.Color.parseColor("#666666")
                }
                val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                canvas.drawText("Fecha: $dateStr | Total: ${products.size} productos", MARGIN, yPosition, infoPaint)
                canvas.drawText("Página ${pageNum + 1} de $totalPages", PAGE_WIDTH - MARGIN, yPosition, infoPaint.apply { textAlign = Paint.Align.RIGHT })
                yPosition += 20f
                
                // Línea separadora
                canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, Paint().apply {
                    color = android.graphics.Color.parseColor("#CCCCCC")
                    strokeWidth = 1f
                })
                yPosition += 10f
                
                // Encabezados de columna
                val headerBgPaint = Paint().apply {
                    color = android.graphics.Color.parseColor("#2196F3")
                }
                val headerY = yPosition
                val headerHeight = 25f
                canvas.drawRect(MARGIN, headerY, PAGE_WIDTH - MARGIN, headerY + headerHeight, headerBgPaint)
                
                var xPosition = MARGIN + 10f
                
                // Producto (200 puntos)
                canvas.drawText("PRODUCTO", xPosition, headerY + 18f, headerPaint)
                xPosition += 200f
                
                // Precio (60 puntos)
                canvas.drawText("PRECIO", xPosition, headerY + 18f, headerPaint.apply { textAlign = Paint.Align.RIGHT })
                xPosition += 60f
                
                // Stock (50 puntos)
                canvas.drawText("STOCK", xPosition, headerY + 18f, headerPaint.apply { textAlign = Paint.Align.RIGHT })
                xPosition += 50f
                
                // Categoría (90 puntos)
                canvas.drawText("CATEGORÍA", xPosition, headerY + 18f, headerPaint.apply { textAlign = Paint.Align.CENTER })
                xPosition += 90f
                
                // Proveedor (resto)
                val supplierWidth = PAGE_WIDTH - MARGIN - xPosition - 10f
                canvas.drawText("PROVEEDOR", xPosition, headerY + 18f, headerPaint.apply { textAlign = Paint.Align.LEFT })
                
                yPosition += headerHeight + 5f
                
                // Dibujar productos
                for (product in pageProducts) {
                    if (yPosition > PAGE_HEIGHT - MARGIN - FOOTER_HEIGHT - LINE_HEIGHT) {
                        break // No hay más espacio en esta página
                    }
                    
                    xPosition = MARGIN + 10f
                    
                    // Producto
                    val productName = if (product.nombreProducto.length > 35) {
                        product.nombreProducto.substring(0, 32) + "..."
                    } else {
                        product.nombreProducto
                    }
                    canvas.drawText(productName, xPosition, yPosition, textPaint)
                    xPosition += 200f
                    
                    // Precio
                    val priceText = String.format("$%.2f", product.precioVenta)
                    canvas.drawText(priceText, xPosition, yPosition, numberPaint.apply { textAlign = Paint.Align.RIGHT })
                    xPosition += 60f
                    
                    // Stock (con color)
                    val stockPaint = if (product.stock < 100) lowStockPaint else normalStockPaint
                    canvas.drawText(product.stock.toString(), xPosition, yPosition, stockPaint)
                    xPosition += 50f
                    
                    // Categoría
                    val categoryText = if (product.categoria.length > 12) {
                        product.categoria.substring(0, 10) + "..."
                    } else {
                        product.categoria
                    }
                    canvas.drawText(categoryText, xPosition, yPosition, textPaint.apply { textAlign = Paint.Align.CENTER })
                    xPosition += 90f
                    
                    // Proveedor
                    val supplierText = if (product.proveedor.length > 25) {
                        product.proveedor.substring(0, 23) + "..."
                    } else {
                        product.proveedor
                    }
                    canvas.drawText(supplierText, xPosition, yPosition, textPaint.apply { textAlign = Paint.Align.LEFT })
                    
                    // Línea separadora
                    yPosition += LINE_HEIGHT + 2f
                    canvas.drawLine(MARGIN + 5f, yPosition, PAGE_WIDTH - MARGIN - 5f, yPosition, Paint().apply {
                        color = android.graphics.Color.parseColor("#E0E0E0")
                        strokeWidth = 0.5f
                    })
                    yPosition += 3f
                }
                
                // Pie de página
                val footerPaint = Paint().apply {
                    textSize = 8f
                    color = android.graphics.Color.parseColor("#999999")
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(
                    "FactuSoft - Sistema de Gestión",
                    PAGE_WIDTH / 2f,
                    PAGE_HEIGHT - MARGIN + 15f,
                    footerPaint
                )
                
                pdfDocument.finishPage(page)
            }
            
            // Escribir el archivo
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
            return null
        }
    }
}

