package Model;

public enum Acesso {
    READ("R"),
    WRITE("W"),
    START("S"),
    END("E"),
    COMMIT("C"),
    ABORT("A");
    public String texto;

    private Acesso(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
