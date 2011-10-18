package VEW.Compiler2;

import java.io.PrintWriter;

import VEW.Common.StringTools;
import VEW.Common.XML.XMLTag;

public class ParticleManagerMaker {
  public static void writeParticleManagerJava(String fileName, XMLTag model) {
    try {
      PrintWriter PW = StringTools.OpenOutputFile(fileName);
      PW.println("package VEW.Sim;");
      PW.println("");
      PW.println("class ParticleManager {");
      PW.println("");
      PW.println("  public static void sortList(FunctionalGroup[] list, int count) {");
      PW.println("  // Sort into... descending cell count");
      PW.println("    for (int i=0; i<count-1; i++) {");
      PW.println("      int index=i;");
      PW.println("      for (int j=i+1; j<count; j++) if (list[j].c[1]>list[index].c[1]) index=j;");
      PW.println("      if (index!=i) {");
      PW.println("        final FunctionalGroup swap = list[index];");
      PW.println("        list[index]=list[i];");
      PW.println("        list[i]=swap;");
      PW.println("      }");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void split(FunctionalGroup[] fgList, int fgSize, int target) {");
      PW.println("    int[] NumberOfSplits = new int[fgSize];");
      PW.println("    sortList(fgList,fgSize);");
      PW.println("    int SplitsNeeded = target - fgSize;");
      PW.println("    int AvailableSplits;");
      PW.println("    int i;");
      PW.println("    if (fgSize == 1) NumberOfSplits[0] = SplitsNeeded;");
      PW.println("    else {");
      PW.println("      for (i = 0; i<fgSize - 1 && SplitsNeeded > 0; i++) {");
      PW.println("        AvailableSplits = (int) Math.floor(fgList[i].c[1] / fgList[i + 1].c[1]);");
      PW.println("        while(AvailableSplits > 0 && SplitsNeeded > 0) {");
      PW.println("          SplitsNeeded--;");
      PW.println("          NumberOfSplits[i]++;");
      PW.println("          for (int j = i - 1; j > -1 && SplitsNeeded > 0; j--) {");
      PW.println("            SplitsNeeded -= NumberOfSplits[j];");
      PW.println("            NumberOfSplits[j] *= 2;");
      PW.println("            if (SplitsNeeded<0) {");
      PW.println("              NumberOfSplits[j] += SplitsNeeded;");
      PW.println("              SplitsNeeded = 0;");
      PW.println("            }");
      PW.println("          }");
      PW.println("          AvailableSplits--;");
      PW.println("        }");
      PW.println("      }");
      PW.println("      while(SplitsNeeded > 0) {");
      PW.println("        SplitsNeeded--;");
      PW.println("        NumberOfSplits[i]++;");
      PW.println("        for (int j = i - 1; j > -1 && SplitsNeeded > 0; j--) {");
      PW.println("          SplitsNeeded -= NumberOfSplits[j];");
      PW.println("          NumberOfSplits[j] *= 2;");
      PW.println("          if (SplitsNeeded<0) {");
      PW.println("            NumberOfSplits[j] += SplitsNeeded;");
      PW.println("            SplitsNeeded = 0;");
      PW.println("          }");
      PW.println("        }");
      PW.println("      }");
      PW.println("    }");
      PW.println("    FunctionalGroup OldParticle;");
      PW.println("    FunctionalGroup NewParticle;");
      PW.println("    for (i=0; i<NumberOfSplits.length && NumberOfSplits[i]>0; i++) {");
      PW.println("      OldParticle = fgList[i];");
      PW.println("      OldParticle.split(1+NumberOfSplits[i]);");
      PW.println("      OldParticle.c[1]-=( OldParticle.c[1] * ((double)NumberOfSplits[i] / (double)(NumberOfSplits[i] + 1)));");
      PW.println("      for (int j = 0; j<NumberOfSplits[i]; j++) {");
      PW.println("        NewParticle = OldParticle.getClone();");
      PW.println("        NewParticle.copyRequests(OldParticle);");
      PW.println("        Kernel.W.addAgent(NewParticle);");
      PW.println("        if ((OldParticle.log && OldParticle.getStageLogStatus()) || (NewParticle.log && NewParticle.getStageLogStatus())) {");
      PW.println("          if (Logging.getLineage())");
      PW.println("            Logging.writeSplitToLineage(OldParticle.getParams()._type, Kernel.myTime - 1, OldParticle.id, NewParticle.id,OldParticle._CurrentStage);");
      PW.println("          if (Logging.getLifespan())");
      PW.println("            Logging.writeSplitToLifespan(OldParticle.getParams()._type, Kernel.myTime - 1, OldParticle.id, NewParticle.id, NewParticle.c[1]);");
      PW.println("        }");
      PW.println("");
      PW.println("        fgList[fgSize] = NewParticle;");
      PW.println("        fgSize++;");
      PW.println("      }");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void merge(FunctionalGroup[] fgList, int fgSize, int target) {");
      PW.println("    if (target == 1) {");
      PW.println("      FunctionalGroup TheOne = fgList[0];");
      PW.println("      FunctionalGroup TheOther;");
      PW.println("      Kernel.W.removeAgent(TheOne,false);");
      PW.println("      for (int i = 1; i<fgSize; i++) {");
      PW.println("        TheOther = fgList[i];");
      PW.println("        Kernel.W.removeAgent(TheOther,false);");
      PW.println("        if ((TheOther.log && TheOther.getStageLogStatus()) || (TheOne.log && TheOne.getStageLogStatus())) {");
      PW.println("          if (Logging.getLineage())");
      PW.println("            Logging.writeMergeToLineage(TheOne.getParams()._type, Kernel.myTime - 1, TheOther.id, TheOne.id,TheOne._CurrentStage);");
      PW.println("          if (Logging.getLifespan())");
      PW.println("            Logging.writeMergeToLifespan(TheOne.getParams()._type, Kernel.myTime - 1, TheOther.id, TheOne.id, TheOther.c[1], TheOne.c[1]);");
      PW.println("        }");
      PW.println("");
      PW.println("        TheOne.mergeWith(TheOther);");
      PW.println("        TheOther.freeParticle();");
      PW.println("      }");
      PW.println("      Kernel.W.addAgent(TheOne);");
      PW.println("      fgSize = 1;");
      PW.println("    } else {");
      PW.println("      int NumberOfMerges = fgSize - target;");
      PW.println("      NumberOfMerges *= 2;");
      PW.println("      int OneEnd;");
      PW.println("      int OtherEnd;");
      PW.println("      FunctionalGroup TheOne;");
      PW.println("      FunctionalGroup TheOther;");
      PW.println("      while(NumberOfMerges > fgSize) {");
      PW.println("        sortList(fgList,fgSize);");
      PW.println("        OneEnd = 0;");
      PW.println("        OtherEnd = fgSize - 1;");
      PW.println("        while(OneEnd<OtherEnd) {");
      PW.println("          TheOne = fgList[OneEnd];");
      PW.println("          TheOther = fgList[OtherEnd];");
      PW.println("          Kernel.W.removeAgent(TheOne,false);");
      PW.println("          Kernel.W.removeAgent(TheOther,false);");
      PW.println("          if ((TheOther.log && TheOther.getStageLogStatus()) || (TheOne.log && TheOne.getStageLogStatus())) {");
      PW.println("            if (Logging.getLineage())");
      PW.println("              Logging.writeMergeToLineage(TheOne.getParams()._type, Kernel.myTime, TheOther.id, TheOne.id,TheOne._CurrentStage);");
      PW.println("            if (Logging.getLifespan())");
      PW.println("              Logging.writeMergeToLifespan(TheOne.getParams()._type, Kernel.myTime, TheOther.id, TheOne.id, TheOther.c[1], TheOne.c[1]);");
      PW.println("          }");
      PW.println("          TheOne.mergeWith(TheOther);");
      PW.println("          TheOther.freeParticle();");
      PW.println("          Kernel.W.addAgent(TheOne);");
      PW.println("          OneEnd++;");
      PW.println("          OtherEnd--;");
      PW.println("          fgSize--;");
      PW.println("        }");
      PW.println("        NumberOfMerges = fgSize - target;");
      PW.println("        NumberOfMerges *= 2;");
      PW.println("      }");
      PW.println("      sortList(fgList,fgSize);");
      PW.println("      OneEnd = fgSize - NumberOfMerges;");
      PW.println("      OtherEnd = fgSize - 1;");
      PW.println("      while(OneEnd<OtherEnd) {");
      PW.println("        TheOne = fgList[OneEnd];");
      PW.println("        TheOther = fgList[OtherEnd];");
      PW.println("        Kernel.W.removeAgent(TheOne,false);");
      PW.println("        Kernel.W.removeAgent(TheOther,false);");
      PW.println("        if ((TheOther.log && TheOther.getStageLogStatus()) || (TheOne.log && TheOne.getStageLogStatus())) {");
      PW.println("          if (Logging.getLineage())");
      PW.println("            Logging.writeMergeToLineage(TheOne.getParams()._type, Kernel.myTime, TheOther.id, TheOne.id,TheOne._CurrentStage);");
      PW.println("          if (Logging.getLifespan())");
      PW.println("            Logging.writeMergeToLifespan(TheOne.getParams()._type, Kernel.myTime, TheOther.id, TheOne.id, TheOther.c[1], TheOne.c[1]);");
      PW.println("        }");
      PW.println("");
      PW.println("        TheOne.mergeWith(TheOther);");
      PW.println("        TheOther.freeParticle();");
      PW.println("        Kernel.W.addAgent(TheOne);");
      PW.println("        OneEnd++;");
      PW.println("        OtherEnd--;");
      PW.println("        fgSize--;");
      PW.println("      }");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static FunctionalGroup[] getLayerParticles(BLayer b, int species, int stage, int size) {");
      PW.println("    FunctionalGroup[] fgList = new FunctionalGroup[size];");
      PW.println("    int agentNo=0;");
      PW.println("    int noAgents=0;");
      PW.println("    while (noAgents<b.AgentCounts[species][stage]) {");
      PW.println("      final FunctionalGroup fg = ((FunctionalGroup)Kernel.W.agents[species][stage].get(agentNo));");
      PW.println("      if (fg.blayer==b) fgList[noAgents++]=fg;");
      PW.println("      agentNo++;");
      PW.println("    }");
      PW.println("    return fgList;");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void splitLayer(BLayer b, int species, int stage, int target) {");
      PW.println("    if ((b.AgentCounts[species][stage]<target) && (b.AgentCounts[species][stage]>0)) {");
      PW.println("      FunctionalGroup[] fgList = getLayerParticles(b,species,stage,target);");
      PW.println("      split(fgList,b.AgentCounts[species][stage],target);");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void mergeLayer(BLayer b, int species, int stage, int target) {");
      PW.println("    if ((b.AgentCounts[species][stage]>target) && (b.AgentCounts[species][stage]>0)) {");
      PW.println("      FunctionalGroup[] fgList = getLayerParticles(b,species,stage,b.AgentCounts[species][stage]);");
      PW.println("      merge(fgList,b.AgentCounts[species][stage],target);");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static int countColumnParticles(WaterCol W, int species, int stage, int top, int bottom) {");
      PW.println("    int total = 0;");
      PW.println("    final int agentCount = W.agents[species][stage].size();");
      PW.println("    for (int i=0; i<agentCount; i++) {");
      PW.println("      final FunctionalGroup fg = (FunctionalGroup) W.agents[species][stage].get(i);");
      PW.println("      if ((fg.blayer.Depth>=top) && (fg.blayer.Depth<=bottom)) total++;");
      PW.println("    }");
      PW.println("    return total;");
      PW.println("  }");
      PW.println("");
      PW.println("  public static int countColumnParticles(WaterCol W, int species, int stage) {");
      PW.println("    return countColumnParticles(W,species,stage,0,W.B_Layer.length);");
      PW.println("  }");
      PW.println("");
      PW.println("  public static FunctionalGroup[] getColumnParticles(WaterCol W, int species, int stage, int size, int top, int bottom) {");
      PW.println("    int count = 0;");
      PW.println("    FunctionalGroup[] fgList = new FunctionalGroup[size];");
      PW.println("    for (int i=0; i<W.AgentCounts[species][stage]; i++) {");
      PW.println("      final FunctionalGroup fg = (FunctionalGroup) W.agents[species][stage].get(i);");
      PW.println("      if ((fg.blayer.Depth>=top) && (fg.blayer.Depth<=bottom)) fgList[count++]=fg;");
      PW.println("    }");
      PW.println("    return fgList;");
      PW.println("  }");
      PW.println("");
      PW.println("  public static FunctionalGroup[] getColumnParticles(WaterCol W, int species, int stage, int size) {");
      PW.println("    return getColumnParticles(W,species,stage,size,0,W.B_Layer.length);");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void splitMixLayer(WaterCol W, int species, int stage, int target) {");
      PW.println("    final int turbocline = (int) Math.ceil(Kernel.W.MLDepth);");
      PW.println("    final int totalTarget = turbocline*target;");
      PW.println("    final int currentParticles = countColumnParticles(W,species,stage,0,turbocline);");
      PW.println("    if ((currentParticles<totalTarget) && (currentParticles>0)) {");
      PW.println("      FunctionalGroup[] fgList = getColumnParticles(W,species,stage,totalTarget,0,turbocline);");
      PW.println("      split(fgList,currentParticles,totalTarget);");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void mergeMixLayer(WaterCol W, int species, int stage, int target) {");
      PW.println("    final int turbocline = (int) Math.ceil(Kernel.W.MLDepth);");
      PW.println("    final int totalTarget = turbocline*target;");
      PW.println("    final int currentParticles = countColumnParticles(W,species,stage,0,turbocline);");
      PW.println("    if ((currentParticles>totalTarget) && (currentParticles>0)) {");
      PW.println("      FunctionalGroup[] fgList = getColumnParticles(W,species,stage,currentParticles,0,turbocline);");
      PW.println("      merge(fgList,currentParticles,target);");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void splitColumn(WaterCol W, int species, int stage, int target) {");
      PW.println("    if ((W.AgentCounts[species][stage]<target) && (W.AgentCounts[species][stage]>0)) {");
      PW.println("      FunctionalGroup[] fgList = getColumnParticles(W,species,stage,target);");
      PW.println("      split(fgList,W.AgentCounts[species][stage],target);");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("  public static void mergeColumn(WaterCol W, int species, int stage, int target) {");
      PW.println("    if ((Kernel.W.AgentCounts[species][stage]>target) && (Kernel.W.AgentCounts[species][stage]>0)) {");
      PW.println("      FunctionalGroup[] fgList = getColumnParticles(W,species,stage,Kernel.W.AgentCounts[species][stage]);");
      PW.println("      merge(fgList,Kernel.W.AgentCounts[species][stage],target);");
      PW.println("    }");
      PW.println("  }");
      PW.println("");
      PW.println("");
      PW.println("  public static void maintainLostParticles(WaterCol W) {");
      PW.println("    for (int species=0; species<W.lostParticles.length; species++) {");
      PW.println("      for (int stage=0; stage<W.lostParticles[species].length; stage++) {");
      PW.println("        if (W.lostParticles[species][stage]>0) {");
      PW.println("          int total = countColumnParticles(W, species, stage);");
      PW.println("          if (total>0) {");
      PW.println("            FunctionalGroup[] fgList = getColumnParticles(W, species, stage, total+W.lostParticles[species][stage]);");
      PW.println("            split(fgList, total, total + W.lostParticles[species][stage]);");
      PW.println("            W.lostParticles[species][stage]=0;");
      PW.println("          }");
      PW.println("        }");
      PW.println("      }");
      PW.println("    }");
      PW.println("  }");
      PW.println("}");
      PW.flush();
      PW.close();
    } catch (Exception e) { e.printStackTrace(); }

  }

}

