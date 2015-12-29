@NOOPERATORS
importPackage(Packages.mayday.jsc.tools);
importClass(Packages.java.lang.Double);

function asMatrix(probelist)
{ 
  return(new ProbeMatrix(probelist.getAllProbes()));
}

DEFINE FOR CLASS:ASSIGNABLE FROM:mayday.core.structures.linalg.matrix.AbstractMatrix
FOR:obj[,experiment]=colData
DO:obj.setColumn(experiment,colData)

DEFINE FOR CLASS:ASSIGNABLE FROM:mayday.core.structures.linalg.matrix.AbstractMatrix
FOR:obj[probe,]=rowData
DO:obj.setRow(probe, rowData)

DEFINE FOR CLASS:ASSIGNABLE FROM:mayday.core.structures.linalg.matrix.AbstractMatrix
FOR:obj[probe,]
DO:obj.getRow(probe)

DEFINE FOR CLASS:ASSIGNABLE FROM:mayday.core.structures.linalg.matrix.AbstractMatrix
FOR:obj[,experiment]
DO:obj.getColumn(experiment)

DEFINE FOR CLASS:ASSIGNABLE FROM:mayday.core.structures.linalg.matrix.AbstractMatrix
FOR:obj[probe, experiment]
DO:obj.getValue(probe, experiment)

DEFINE FOR CLASS:ASSIGNABLE FROM:mayday.core.structures.linalg.matrix.AbstractMatrix
FOR:obj[probe, experiment]=value
DO:obj.setValue(probe, experiment, value)
