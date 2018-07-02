SELECT 
<#list columns as col>
	${col.tableAlias}.${col.name}<#if (col_index+1 < columns?size)>,</#if>
</#list>
from ${masterTable.schemaName}.${masterTable.tagTableNameEn} ${masterTable.alias}
<#list tables as tab>
<#if tab.joinType == '1'>LEFT<#else>INNER</#if> JOIN ${tab.schemaName}.${tab.tableName} ${tab.alias} ON ${tab.alias}.${tab.slaveTableColumnName} = ${masterTable.alias}.${tab.masterTableColumnName}
</#list>
<#if (company?size > 0)>
INNER JOIN MKTVIEW.STD_MKTOL_AREA_TREE_NO_DEPT_Z tab1 ON t0.area_id = tab1.area_id
</#if>
WHERE 1=1
<#if (company?size > 0)>
	AND (
	<#list company as c>
tab1.area_id_lv${c.level} = ${c.companyId} <#if (c_index+1 < company?size)>OR</#if>
	</#list> )
</#if>
<#list triggers as t>
<#if t.useType != '2'>
AND ${resolveCond(t.leftOperand,t.conditionType,t.operator,t.rightOperand,t.dataType)}
</#if>
</#list>
