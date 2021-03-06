package com.easytoolsoft.easyreport.engine;

import java.util.List;
import java.util.Map;

import com.easytoolsoft.easyreport.engine.data.AbstractReportDataSet;
import com.easytoolsoft.easyreport.engine.data.ColumnTree;
import com.easytoolsoft.easyreport.engine.data.ColumnTreeNode;
import com.easytoolsoft.easyreport.engine.data.ReportDataCell;
import com.easytoolsoft.easyreport.engine.data.ReportDataColumn;
import com.easytoolsoft.easyreport.engine.data.ReportDataRow;
import com.easytoolsoft.easyreport.engine.data.ReportParameter;
import com.easytoolsoft.easyreport.engine.util.NumberUtils;

/**
 * 纵向展示统计列的报表生成类
 *
 * @author tomdeng
 */
public class VerticalStatColumnReportBuilder extends AbstractReportBuilder implements ReportBuilder {

	/**
	 * 纵向展示统计列的报表生成类
	 *
	 * @param reportDataSet
	 *            报表数据集
	 * @param reportParameter
	 *            报表参数
	 */
	public VerticalStatColumnReportBuilder(final AbstractReportDataSet reportDataSet, final ReportParameter reportParameter) {
		super(reportDataSet, reportParameter);
	}

	@Override
	public void drawTableBodyRows() {
		final ColumnTree leftFixedColumnTree = this.reportDataSet.getBodyLeftFixedColumnTree();
		final List<ColumnTreeNode> rowNodes = leftFixedColumnTree.getLastLevelNodes();
		final List<ColumnTreeNode> columnNodes = this.reportDataSet.getBodyRightColumnNodes();
		final Map<String, ReportDataRow> dataRowMap = this.reportDataSet.getRowMap();
		final Map<String, ColumnTreeNode> treeNodePathMap = this.getTreeNodePathMap(leftFixedColumnTree);
		final String defaultColName = this.reportDataSet.getEnabledStatColumns().get(0).getName();
		final boolean isHideStatCol = this.reportDataSet.isHideStatColumn();

		int rowIndex = 0;
		String[] lastNodePaths = null;
		//
		LinkFunc linkFunc = null;
		boolean showDataLinks = this.reportParameter.shouldShowDataLinks();
		String reportCode = this.reportParameter.getUcode();
		boolean ignore0LinkFunc = false;
		//
		this.tableRows.append("<tbody>");
		for (final ColumnTreeNode rowNode : rowNodes) {
			final String colName = isHideStatCol ? defaultColName : rowNode.getName();
			this.tableRows.append("<tr").append(rowIndex % 2 == 0 ? " class=\"easyreport-row\"" : "").append(">");
			lastNodePaths = this.drawLeftFixedColumn(treeNodePathMap, lastNodePaths, rowNode, this.reportParameter.isRowSpan());
			for (final ColumnTreeNode columnNode : columnNodes) {
				final String rowKey = this.reportDataSet.getRowKey(rowNode, columnNode);
				ReportDataRow dataRow = dataRowMap.get(rowKey);
				if (dataRow == null) {
					dataRow = new ReportDataRow();
				}
				final ReportDataCell cell = dataRow.getCell(colName);
				String valText = (cell == null) ? "" : cell.toString();
				ReportDataColumn dataColumn = columnNode.getColumn();
				linkFunc = dataColumn.getLinkFunc();
				ignore0LinkFunc = dataColumn.ignore0LinkFunc();
				if (showDataLinks && linkFunc != null && valText.length() > 0) {
					if (!ignore0LinkFunc || !NumberUtils.isNumVal0(valText)) {
						valText = LinkFunc.toLinkHtml(valText, linkFunc, reportCode, colName, dataRow.getDataMap());
					}
				}
				String style = cell == null ? "" : cell.getStyle();
				this.tableRows.append(String.format("<td style=\"%s\">", style)).append(valText).append("</td>");
			}
			this.tableRows.append("</tr>");
			rowIndex++;
		}
		this.tableRows.append("</tbody>");
	}

	@Override
	public void drawTableFooterRows() {
	}
}
