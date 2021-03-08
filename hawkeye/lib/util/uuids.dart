class UUIDS {
  static String withoutDashes(String uuid) => uuid.replaceAll("-", "");

  static String withDashes(String c) =>
      "${c.substring(0, 8)}-${c.substring(8, 12)}-${c.substring(12, 16)}-${c.substring(16, 20)}-${c.substring(20)}";
}
