package dao;

import java.util.ArrayList;
import java.util.Collections;

import dao.ManosDAO.Mazo;

public class prueba {
	
	public static void main(String args[]) {
		ManosDAO facade = new ManosDAO();
		ArrayList<Mazo> manocontraria = facade.devolverMano("Alvaro");
		ArrayList<String> manocontraria_s = new ArrayList<String>();
		for (int i = 0; i < manocontraria.size(); i++) {
			for(int j=0;j<manocontraria.get(i).getNum();j++) {
				manocontraria_s.add(manocontraria.get(i).getId_carta());
			}
		}
		Collections.shuffle(manocontraria_s);
		System.out.println(manocontraria_s.get(0));
		facade.modificarMano("Yorx", manocontraria_s.get(0),"anyadir");
		facade.modificarMano("Alvaro", manocontraria_s.get(0),"eliminar");
	}
}
