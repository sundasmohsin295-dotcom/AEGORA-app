package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

data class AsmInstruction(
  val address: String,
  val mnemonic: String,
  val operands: String,
  val bytes: String,
  val comment: String = "",
  val isVulnerable: Boolean = false,
  val isBranchTarget: Boolean = false
)

data class BasicBlock(
  val id: String,
  val title: String,
  val instructions: List<AsmInstruction>,
  val trueBranchTargetId: String? = null,
  val falseBranchTargetId: String? = null,
  val xOffsetRatio: Float = 0.5f,
  val yOffsetRatio: Float = 0.5f
)

data class RegisterState(
  val rax: String = "0x0000000000000000",
  val rbx: String = "0x00007ffe8b42a010",
  val rcx: String = "0x0000000000000028",
  val rdx: String = "0x000055c829e13040",
  val rsi: String = "0x00007ffe8b429f00",
  val rdi: String = "0x0000000000000001",
  val rbp: String = "0x00007ffe8b429ee0",
  val rsp: String = "0x00007ffe8b429e80",
  val rip: String = "0x000055c829e1124f",
  val eflags: String = "[CF=0 ZF=1 SF=0 OF=0]"
)

data class HexByteEntry(
  val offset: String,
  val hexBytes: List<String>,
  val asciiRepresentation: String,
  val tag: String? = null,
  val tagColor: Color? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinaryDisassemblerScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0: CFG Disassembler, 1: Hex Inspector
  var selectedBlockId by remember { mutableStateOf("vuln_func") }
  var selectedInstructionIndex by remember { mutableIntStateOf(2) }
  val activeBreakpoints = remember { mutableStateListOf("0x55c829e11261") }
  var registerState by remember { mutableStateOf(RegisterState()) }
  var executionStep by remember { mutableIntStateOf(0) }
  var selectedHexRange by remember { mutableStateOf<String?>("0x00000010: NOP Sled [0x90 x 16]") }

  val basicBlocks = remember {
    listOf(
      BasicBlock(
        id = "main",
        title = "sub_main (0x55c829e111a0)",
        instructions = listOf(
          AsmInstruction("0x55c829e111a0", "push", "rbp", "55", "Save frame pointer"),
          AsmInstruction("0x55c829e111a1", "mov", "rbp, rsp", "48 89 e5", "Setup stack frame"),
          AsmInstruction("0x55c829e111a4", "sub", "rsp, 0x20", "48 83 ec 20", "Allocate 32 bytes on stack"),
          AsmInstruction("0x55c829e111a8", "call", "vuln_auth_check", "e8 43 00 00 00", "Invoke vulnerable parser")
        ),
        trueBranchTargetId = "vuln_func",
        falseBranchTargetId = null
      ),
      BasicBlock(
        id = "vuln_func",
        title = "vuln_auth_check (0x55c829e1124f)",
        instructions = listOf(
          AsmInstruction("0x55c829e1124f", "push", "rbp", "55", "Stack setup"),
          AsmInstruction("0x55c829e11250", "mov", "rbp, rsp", "48 89 e5", "Set base pointer"),
          AsmInstruction("0x55c829e11253", "lea", "rax, [rbp-0x40]", "48 8d 45 c0", "Load stack buffer pointer (64B)", isVulnerable = true),
          AsmInstruction("0x55c829e11257", "mov", "rdi, rax", "48 89 c7", "Arg1 = buffer destination"),
          AsmInstruction("0x55c829e1125a", "call", "strcpy@plt", "e8 81 fe ff ff", "UNCHECKED BUFFER COPY (VULN: CVE-2024-BOF)", isVulnerable = true),
          AsmInstruction("0x55c829e1125f", "cmp", "dword ptr [rbp-0x4], 0x1337", "81 7d fc 37 13 00 00", "Check auth flag value"),
          AsmInstruction("0x55c829e11266", "je", "grant_root_shell", "74 12", "Branch taken if cookie hijacked")
        ),
        trueBranchTargetId = "grant_root_shell",
        falseBranchTargetId = "deny_access"
      ),
      BasicBlock(
        id = "grant_root_shell",
        title = "grant_root_shell (0x55c829e1127a)",
        instructions = listOf(
          AsmInstruction("0x55c829e1127a", "lea", "rdi, [rip+0xe83]", "48 8d 3d 83 0e 00 00", "Arg1 = \"/bin/sh\""),
          AsmInstruction("0x55c829e11281", "xor", "rsi, rsi", "48 31 f6", "Arg2 = NULL"),
          AsmInstruction("0x55c829e11284", "call", "execve@plt", "e8 67 fe ff ff", "SPAWN PRIVILEGED SHELL", isVulnerable = true)
        ),
        trueBranchTargetId = null,
        falseBranchTargetId = null
      ),
      BasicBlock(
        id = "deny_access",
        title = "deny_access (0x55c829e11290)",
        instructions = listOf(
          AsmInstruction("0x55c829e11290", "lea", "rdi, [rip+0xec0]", "48 8d 3d c0 0e 00 00", "Arg1 = \"Access Denied\""),
          AsmInstruction("0x55c829e11297", "call", "puts@plt", "e8 34 fe ff ff", "Print rejection"),
          AsmInstruction("0x55c829e1129c", "xor", "eax, eax", "31 c0", "Return 0"),
          AsmInstruction("0x55c829e1129e", "ret", "", "c3", "Return")
        ),
        trueBranchTargetId = null,
        falseBranchTargetId = null
      )
    )
  }

  val hexDumpData = remember {
    listOf(
      HexByteEntry("00000000", listOf("7f", "45", "4c", "46", "02", "01", "01", "00", "00", "00", "00", "00", "00", "00", "00", "00"), ".ELF............", "ELF64 Header", CyberCyan),
      HexByteEntry("00000010", listOf("90", "90", "90", "90", "90", "90", "90", "90", "90", "90", "90", "90", "90", "90", "90", "90"), "................", "NOP Sled (0x90)", CyberEmerald),
      HexByteEntry("00000020", listOf("41", "41", "41", "41", "41", "41", "41", "41", "41", "41", "41", "41", "41", "41", "41", "41"), "AAAAAAAAAAAAAAAA", "Buffer Overflow Padding", CyberAmber),
      HexByteEntry("00000030", listOf("41", "41", "41", "41", "41", "41", "41", "41", "37", "13", "00", "00", "00", "00", "00", "00"), "AAAAAAAA7.......", "Overwritten Cookie (0x1337)", NeonCrimson),
      HexByteEntry("00000040", listOf("7a", "12", "e1", "29", "c8", "55", "00", "00", "00", "00", "00", "00", "00", "00", "00", "00"), "z..).U..........", "Hijacked RIP -> grant_root_shell", NeonPink),
      HexByteEntry("00000050", listOf("31", "c0", "48", "bb", "d1", "9d", "96", "91", "d0", "8c", "97", "ff", "48", "f7", "db", "53"), "1.H.........H..S", "x86_64 Shellcode Stager", NeonViolet)
    )
  }

  Scaffold(
    topBar = {
      Surface(
        color = CyberDarkSlate,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column {
          TopAppBar(
            title = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(1.dp, NeonCyan, RoundedCornerShape(8.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Code, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    "BINARY DISASSEMBLER & CFG",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    color = TextPrimaryDark
                  )
                  Text(
                    "ELF64 • ASLR: OFF • Canary: Hijacked",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = CyberCyan
                  )
                }
              }
            },
            navigationIcon = {
              IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
              }
            },
            actions = {
              IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                executionStep++
                registerState = registerState.copy(
                  rip = "0x55c829e1127a",
                  rax = "0x0000000000001337",
                  rsp = "0x00007ffe8b429e38"
                )
              }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Step Next", tint = CyberEmerald)
              }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkSlate)
          )

          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberDarkSlate,
            contentColor = NeonCyan,
            divider = { HorizontalDivider(color = CyberBorder) }
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              text = { Text("CONTROL-FLOW GRAPH", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              text = { Text("RAW HEX DISSECTOR", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
          }
        }
      }
    },
    containerColor = CyberBlack
  ) { innerPadding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(CyberBlack)
    ) {
      if (selectedTab == 0) {
        // Control-Flow Graph & Instruction Inspector
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Floating Live Register State HUD
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = CyberCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  "REGISTER HUD (x86_64)",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                  color = NeonCyan
                )
                Text(
                  registerState.eflags,
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = CyberEmerald
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  RegisterRow("RAX", registerState.rax, NeonCyan)
                  RegisterRow("RBX", registerState.rbx, TextSecondaryDark)
                  RegisterRow("RCX", registerState.rcx, TextSecondaryDark)
                }
                Column {
                  RegisterRow("RDI", registerState.rdi, TextSecondaryDark)
                  RegisterRow("RSI", registerState.rsi, TextSecondaryDark)
                  RegisterRow("RDX", registerState.rdx, TextSecondaryDark)
                }
                Column {
                  RegisterRow("RBP", registerState.rbp, CyberAmber)
                  RegisterRow("RSP", registerState.rsp, NeonCrimson)
                  RegisterRow("RIP", registerState.rip, NeonPink)
                }
              }
            }
          }

          // Flowchart / CFG Canvas Visualizer
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF06060A),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val w = size.width
              val h = size.height

              // Grid background dots
              val step = 20.dp.toPx()
              for (x in 0..(w / step).toInt()) {
                for (y in 0..(h / step).toInt()) {
                  drawCircle(
                    color = CyberBorder.copy(alpha = 0.5f),
                    radius = 1.dp.toPx(),
                    center = Offset(x * step, y * step)
                  )
                }
              }

              // Nodes coordinates
              val nodeMain = Offset(w * 0.25f, h * 0.3f)
              val nodeVuln = Offset(w * 0.50f, h * 0.4f)
              val nodeGrant = Offset(w * 0.82f, h * 0.25f)
              val nodeDeny = Offset(w * 0.82f, h * 0.75f)

              // Branch paths
              // Main -> Vuln (Cyan)
              drawLine(
                color = NeonCyan,
                start = nodeMain,
                end = nodeVuln,
                strokeWidth = 2.dp.toPx()
              )

              // Vuln -> Grant (Green JE taken)
              val pathGrant = Path().apply {
                moveTo(nodeVuln.x, nodeVuln.y)
                cubicTo(nodeVuln.x + 40f, nodeVuln.y - 20f, nodeGrant.x - 40f, nodeGrant.y, nodeGrant.x, nodeGrant.y)
              }
              drawPath(path = pathGrant, color = CyberEmerald, style = Stroke(width = 2.5.dp.toPx()))

              // Vuln -> Deny (Crimson JNE not taken)
              val pathDeny = Path().apply {
                moveTo(nodeVuln.x, nodeVuln.y)
                cubicTo(nodeVuln.x + 40f, nodeVuln.y + 20f, nodeDeny.x - 40f, nodeDeny.y, nodeDeny.x, nodeDeny.y)
              }
              drawPath(
                path = pathDeny,
                color = NeonCrimson.copy(alpha = 0.6f),
                style = Stroke(
                  width = 2.dp.toPx(),
                  pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
              )

              // Draw node boxes
              fun drawNode(center: Offset, label: String, color: Color, isSelected: Boolean) {
                drawCircle(
                  color = color,
                  radius = if (isSelected) 10.dp.toPx() else 7.dp.toPx(),
                  center = center
                )
                if (isSelected) {
                  drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = 16.dp.toPx(),
                    center = center
                  )
                }
              }

              drawNode(nodeMain, "main", NeonCyan, selectedBlockId == "main")
              drawNode(nodeVuln, "vuln_check", CyberAmber, selectedBlockId == "vuln_func")
              drawNode(nodeGrant, "grant_root (je)", CyberEmerald, selectedBlockId == "grant_root_shell")
              drawNode(nodeDeny, "deny (jne)", NeonCrimson, selectedBlockId == "deny_access")
            }
          }

          // Node selector chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            basicBlocks.forEach { block ->
              val isSelected = selectedBlockId == block.id
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else CyberBorder),
                modifier = Modifier.clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  selectedBlockId = block.id
                }
              ) {
                Text(
                  text = block.title.substringBefore(" "),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (isSelected) NeonCyan else TextSecondaryDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }

          // Assembly Basic Block Listing
          val currentBlock = basicBlocks.find { it.id == selectedBlockId } ?: basicBlocks.first()
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  currentBlock.title,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = CyberEmerald
                )
                Text(
                  "${currentBlock.instructions.size} instructions",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = TextSecondaryDark
                )
              }
            }

            items(currentBlock.instructions) { inst ->
              val isBp = activeBreakpoints.contains(inst.address)
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (inst.isVulnerable) NeonCrimson.copy(alpha = 0.12f) else CyberCardBg,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (inst.isVulnerable) NeonCrimson.copy(alpha = 0.6f) else if (isBp) NeonPink else CyberBorder
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (activeBreakpoints.contains(inst.address)) {
                      activeBreakpoints.remove(inst.address)
                    } else {
                      activeBreakpoints.add(inst.address)
                    }
                  }
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Breakpoint dot
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .clip(CircleShape)
                      .background(if (isBp) NeonPink else Color.Transparent)
                      .border(1.dp, if (isBp) NeonPink else TextTertiaryDark, CircleShape)
                  )
                  Spacer(modifier = Modifier.width(8.dp))

                  // Address
                  Text(
                    inst.address.takeLast(6),
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                    color = CyberCyan
                  )
                  Spacer(modifier = Modifier.width(8.dp))

                  // Bytes
                  Text(
                    inst.bytes.padEnd(12),
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                    color = TextTertiaryDark,
                    modifier = Modifier.width(70.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))

                  // Instruction
                  Column(modifier = Modifier.weight(1f)) {
                    Row {
                      Text(
                        inst.mnemonic.padEnd(6),
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Bold
                        ),
                        color = if (inst.isVulnerable) NeonCrimson else TextPrimaryDark
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        inst.operands,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = if (inst.isVulnerable) NeonPink else CyberAmber
                      )
                    }
                    if (inst.comment.isNotBlank()) {
                      Text(
                        "# ${inst.comment}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.sp
                        ),
                        color = if (inst.isVulnerable) NeonCrimson else TextSecondaryDark
                      )
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // Raw Hex Byte Dissector Tab
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = CyberCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                "INTERACTIVE PCAP / BYTE STREAM",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = NeonCyan
              )
              Text(
                selectedHexRange ?: "Tap any byte sequence to inspect vulnerability payload structure.",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                color = CyberEmerald
              )
            }
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(hexDumpData) { entry ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (entry.tagColor != null) entry.tagColor.copy(alpha = 0.08f) else CyberCardBg,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  entry.tagColor?.copy(alpha = 0.5f) ?: CyberBorder
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedHexRange = "${entry.offset}: ${entry.tag ?: "Raw Payload Bytes"}"
                  }
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  if (entry.tag != null) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        entry.tag,
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp
                        ),
                        color = entry.tagColor ?: CyberCyan
                      )
                      Text(
                        "Offset: ${entry.offset}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.sp
                        ),
                        color = TextTertiaryDark
                      )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      entry.offset,
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                      color = CyberCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                      entry.hexBytes.chunked(8).joinToString("  ") { it.joinToString(" ") },
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                      color = entry.tagColor ?: TextPrimaryDark,
                      modifier = Modifier.weight(1f)
                    )

                    Text(
                      entry.asciiRepresentation,
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                      color = CyberAmber
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun RegisterRow(name: String, value: String, color: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 1.dp)
  ) {
    Text(
      text = "$name:",
      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold),
      color = color,
      modifier = Modifier.width(32.dp)
    )
    Text(
      text = value.takeLast(8),
      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
      color = TextPrimaryDark
    )
  }
}
