import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { KwetterService } from '../../service/kwetter.service';
import { Kwetter } from 'src/app/model/kwetter';

@Component({
  selector: 'app-search-kwetter',
  templateUrl: './search-kwetter.component.html',
  styleUrls: ['./search-kwetter.component.css']
})
export class SearchKwetterComponent implements OnInit {
  searchKwetterForm: FormGroup;
  submitted = false;
  returnUrl: string;
  kwetters: Kwetter[];

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute, private router: Router,
    private searchKwetterService: KwetterService) {
      this.kwetters = [];
  }

  get f() {
    return this.searchKwetterForm.controls;
  }

  ngOnInit() {
    this.searchKwetterForm = this.formBuilder.group({
      text: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.searchKwetterForm.invalid) {
      return;
    }

    this.searchKwetterService.searchKwetter(this.f.text.value).subscribe(data => {
      this.kwetters = [];
      this.kwetters = data;
      this.submitted = true;
      this.ngOnInit();
    });
  }
}