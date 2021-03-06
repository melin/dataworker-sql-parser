package com.dataworker.sql.parser.arithmetic

import com.dataworker.sql.parser.SQLParserException
import com.dataworker.sql.parser.StatementType
import com.dataworker.sql.parser.antlr4.arithmetic.ArithmeticBaseVisitor
import com.dataworker.sql.parser.antlr4.arithmetic.ArithmeticParser
import com.dataworker.sql.parser.model.ArithmeticData
import com.dataworker.sql.parser.model.StatementData
import org.antlr.v4.runtime.tree.ParseTree
import org.apache.commons.lang3.StringUtils

/**
 * Created by libinsong on 2020/7/28 9:49 上午
 */
class ArithmeticAntlr4Visitor(val bracketEnbled: Boolean): ArithmeticBaseVisitor<StatementData>() {

    private var data: StatementData? = null

    private val arithmetic = ArithmeticData()

    override fun visit(tree: ParseTree): StatementData? {
        super.visit(tree)

        if (data == null) {
            throw SQLParserException("不支持的表达式")
        }

        return data;
    }

    override fun visitExpression(ctx: ArithmeticParser.ExpressionContext): StatementData? {
        data = StatementData(StatementType.ARITHMETIC, arithmetic)
        return super.visitExpression(ctx)
    }

    override fun visitIdentifier(ctx: ArithmeticParser.IdentifierContext): StatementData? {
        val name = ctx.text
        if (!arithmetic.functions.contains(name)) {
            if (bracketEnbled) {
                arithmetic.variables.add(StringUtils.substringBetween(name, "[", "]"))
            } else {
                arithmetic.variables.add(name)
            }
        }
        return super.visitIdentifier(ctx)
    }

    override fun visitFunctionName(ctx: ArithmeticParser.FunctionNameContext): StatementData? {
        val parent = ctx.parent as ArithmeticParser.FunctionCallContext
        val name = ctx.text

        if (parent.OVER() != null) {
            arithmetic.functions.add(name + "#over")
        } else {
            arithmetic.functions.add(name)
        }
        return super.visitFunctionName(ctx)
    }
}
