import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ArrayUtil
import com.addzero.addl.FieldDTO
import javax.swing.table.DefaultTableModel

class FieldsTableModel : DefaultTableModel() {
    private val columnNames = arrayOf("Java Type", "Column Name", "Column Comment")

    // 使用可变的字段列表
    var fields: MutableList<FieldDTO> = emptyList<FieldDTO>().toMutableList()

    init {
        setColumnIdentifiers(columnNames)  // 使用列名初始化
    }

    override fun getRowCount(): Int {
        if (null == fields) {
            return 0
        }
        return fields.size
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        val field = fields[rowIndex]
        return when (columnIndex) {
            0 -> field.javaType
            1 -> field.fieldName
            2 -> field.fieldChineseName
            else -> null
        }
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val field = CollUtil.get(fields, rowIndex)
        if (field != null) {
            when (columnIndex) {
                0 -> field.javaType = aValue as String
                1 -> field.fieldName = aValue as String
                2 -> field.fieldChineseName = aValue as String
            }
        }
        fireTableCellUpdated(rowIndex, columnIndex)
    }

    // 添加字段的方法
    fun addField(field: FieldDTO) {
        fields.add(field)
        fireTableRowsInserted(fields.size - 1, fields.size - 1)
    }
}