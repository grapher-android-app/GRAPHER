package util

import algorithms.GraphInformation
import model.Edge
import model.Node
import org.jgrapht.graph.SimpleGraph
import java.util.*

/**
 * Utility class that handles the translation of the graph
 * into the exported format. It can handle Tikz and Metapost
 */
object GraphExporter {

    fun getBeginDocument() : String {
        var res = "\\documentclass{article}"
        res += "\n\\usepackage{tikz}"
        res += "\n\\begin{document}\n"
        return res
    }

    fun getEndDocument() : String {
        return "\\end{document}\n"
    }

    fun getBeginFigure() : String {
        return "\n\\begin{figure}\n\t\\centering"
    }

    fun getEndFigure(graph: SimpleGraph<Node, Edge<Node>>) : String {
        val info : String = GraphInformation.graphInfo(graph)
        //TODO fix this mess that was the original implementation
        val infop : String = info.replace(" ", "-")
                            .replace(",", "-")
                            .replace("\\.", "-")
                            .replace("----", "-")
                            .replace("---", "-")
                            .replace("--", "-")
                            .toLowerCase(Locale.ROOT)
        var result = ""
        result += "\n\t\\caption{$info}\n"
        result += "\t\\label{fig:$infop}\n"
        result += "\\end{figure}\n"
        return result
    }

    fun getMetapost(graph : SimpleGraph<Node, Edge<Node>>) : String {
        var res = "%Metapostified\n\n"
        res += "pair [];\n"
        res += "numeric skalering;\n\n"
        res += "skalering := 0.3"
        res += "vardef drawvertex(expr i) =\n"
        res += "    dotlabel.urt(decimal i, skalering*n[i]);\n"
        res += "enddef;\n\n"

        res += "vardef drawedge (expr inn, ut) =\n"
        res += "    draw (skalering*inn) -- (skalering*ut);\n"
        res += "enddef;\n\n"

        for (v : Node in graph.vertexSet()) {
            res += "n ${v.getId()} = (${v.getCoordinate().getX()},${v.getCoordinate().getY()});\n"
            res += "\n"
        }
        for (e : Edge<Node> in graph.edgeSet()) {
            res += "drawedge(n${e.getSource().getId()}, n${e.getTarget().getId()});\n"
        }

        res += "for i = 1 step 1 until 11:\n"
        res += "    drawvertex(i);\n"
        res += "endfor;\n"

        return res
    }

    // TODO Implement Tikz
}